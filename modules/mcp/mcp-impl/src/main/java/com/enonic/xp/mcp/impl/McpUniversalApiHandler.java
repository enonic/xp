package com.enonic.xp.mcp.impl;

import java.io.IOException;
import java.util.List;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.MediaType;

import jakarta.servlet.ServletException;

import com.enonic.xp.portal.universalapi.UniversalApiHandler;
import com.enonic.xp.server.VersionInfo;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.ServletResponseBody;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

import io.modelcontextprotocol.json.jackson2.JacksonMcpJsonMapper;
import io.modelcontextprotocol.json.schema.jackson2.DefaultJsonSchemaValidator;
import io.modelcontextprotocol.modern.McpException;
import io.modelcontextprotocol.modern.McpSchema.CacheScope;
import io.modelcontextprotocol.modern.McpSchema.Implementation;
import io.modelcontextprotocol.modern.server.McpRequestContext;
import io.modelcontextprotocol.modern.server.McpServer;
import io.modelcontextprotocol.modern.server.feature.McpSyncTool;
import io.modelcontextprotocol.modern.server.feature.McpSyncToolRepository;
import io.modelcontextprotocol.modern.server.feature.ToolsFeature;
import io.modelcontextprotocol.modern.server.feature.ToolsPage;
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
    public McpUniversalApiHandler()
    {
        this( new EmptyTools() );
    }

    McpUniversalApiHandler( final McpSyncToolRepository repository )
    {
        final var objectMapper = new ObjectMapper();
        final var jsonMapper = new JacksonMcpJsonMapper( objectMapper );
        final var server = McpServer.builder()
            .serverInfo( Implementation.builder( "Enonic XP", VersionInfo.get().getVersion() ).build() )
            .jsonMapper( jsonMapper )
            .feature( ToolsFeature.ofSync( repository, jsonMapper, new DefaultJsonSchemaValidator( objectMapper ),
                                          0L, CacheScope.PRIVATE ) )
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
        final ServletResponseBody responseBody = ( rawRequest, response ) -> {
            try
            {
                transport.service( body instanceof String text ? new McpServletRequest( rawRequest, text ) : rawRequest, response );
            }
            catch ( ServletException e )
            {
                throw new IOException( "Failed to serve MCP request", e );
            }
        };
        return WebResponse.create().contentType( MediaType.JSON_UTF_8 ).body( responseBody ).build();
    }

    @Deactivate
    public void deactivate()
    {
        transport.closeGracefully();
    }

    private static final class EmptyTools
        implements McpSyncToolRepository
    {
        @Override
        public ToolsPage list( final McpRequestContext context, final @Nullable String cursor )
        {
            if ( cursor != null )
            {
                throw McpException.invalidParams( "Unknown tools cursor" );
            }
            return ToolsPage.of( List.of() );
        }

        @Override
        public @Nullable McpSyncTool find( final McpRequestContext context, final String name )
        {
            return null;
        }
    }
}
