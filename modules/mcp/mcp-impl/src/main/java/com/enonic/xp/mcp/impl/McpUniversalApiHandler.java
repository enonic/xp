package com.enonic.xp.mcp.impl;

import java.io.IOException;
import java.io.UncheckedIOException;

import org.jspecify.annotations.NullMarked;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.MediaType;

import jakarta.servlet.ServletException;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.universalapi.UniversalApiHandler;
import com.enonic.xp.server.VersionInfo;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.ServletResponseBody;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

import io.modelcontextprotocol.json.jackson2.JacksonMcpJsonMapper;
import io.modelcontextprotocol.json.schema.jackson2.DefaultJsonSchemaValidator;
import io.modelcontextprotocol.modern.McpSchema.CacheScope;
import io.modelcontextprotocol.modern.McpSchema.Implementation;
import io.modelcontextprotocol.modern.server.McpServer;
import io.modelcontextprotocol.modern.server.feature.McpChangeFeed;
import io.modelcontextprotocol.modern.server.feature.McpChangePublisher;
import io.modelcontextprotocol.modern.server.feature.McpSyncToolRepository;
import io.modelcontextprotocol.modern.server.feature.ToolsFeature;
import io.modelcontextprotocol.modern.server.transport.HttpServletMcpTransport;

@NullMarked
@Component(service = UniversalApiHandler.class,
    property = {"key=system:mcp", "title=MCP API", "allowedPrincipals=role:system.everyone", "mount=web"})
public final class McpUniversalApiHandler
    implements UniversalApiHandler
{
    static final String ENDPOINT = "/api/system:mcp";

    private final HttpServletMcpTransport transport;

    @Activate
    public McpUniversalApiHandler( @Reference final McpToolCatalog catalog )
    {
        this( catalog, catalog.changes() );
    }

    McpUniversalApiHandler( final McpSyncToolRepository repository )
    {
        this( repository, new McpChangeFeed() );
    }

    McpUniversalApiHandler( final McpSyncToolRepository repository, final McpChangePublisher changes )
    {
        final var objectMapper = new ObjectMapper();
        final var jsonMapper = new JacksonMcpJsonMapper( objectMapper );
        final var server = McpServer.builder()
            .serverInfo( Implementation.builder( "Enonic XP", VersionInfo.get().getVersion() ).build() )
            .jsonMapper( jsonMapper )
            .feature( ToolsFeature.ofSync( repository, jsonMapper, new DefaultJsonSchemaValidator( objectMapper ),
                                          0L, CacheScope.PRIVATE ) )
            .subscriptions( changes )
            .build();
        this.transport = HttpServletMcpTransport.builder( server ).jsonMapper( jsonMapper ).endpoint( ENDPOINT ).build();
    }

    @Override
    public WebResponse handle( final WebRequest request )
    {
        if ( !ENDPOINT.equals( request.getRawPath() ) )
        {
            return WebResponse.create().status( HttpStatus.NOT_FOUND ).build();
        }
        final var body = request.getBody();
        final var portalRequest = request instanceof PortalRequest portal ? portal : new PortalRequest( request );
        final ServletResponseBody responseBody = ( rawRequest, response ) -> {
            try
            {
                PortalRequestAccessor.runWith( portalRequest, () -> {
                    try
                    {
                        transport.service( body instanceof String text ? new McpServletRequest( rawRequest, text ) : rawRequest, response );
                    }
                    catch ( IOException e )
                    {
                        throw new UncheckedIOException( e );
                    }
                    catch ( ServletException e )
                    {
                        throw new UncheckedIOException( new IOException( "Failed to serve MCP request", e ) );
                    }
                } );
            }
            catch ( UncheckedIOException e )
            {
                throw e.getCause();
            }
        };
        return WebResponse.create().contentType( MediaType.JSON_UTF_8 ).body( responseBody ).build();
    }

    @Deactivate
    public void deactivate()
    {
        transport.closeGracefully();
    }
}
