package com.enonic.xp.mcp.impl;

import java.io.IOException;
import java.io.UncheckedIOException;

import org.jspecify.annotations.NullMarked;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;


import com.google.common.net.MediaType;

import jakarta.servlet.ServletException;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.universalapi.UniversalApiHandler;

import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.ServletResponseBody;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;


import io.modelcontextprotocol.modern.server.feature.McpChangeFeed;
import io.modelcontextprotocol.modern.server.feature.McpChangePublisher;
import io.modelcontextprotocol.modern.server.feature.McpSyncToolRepository;

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
    public McpUniversalApiHandler( @Reference final McpServerFactory serverFactory )
    {
        this.transport = serverFactory.createTransport( ENDPOINT );
    }

    McpUniversalApiHandler( final McpSyncToolRepository repository )
    {
        this( repository, new McpChangeFeed() );
    }

    McpUniversalApiHandler( final McpSyncToolRepository repository, final McpChangePublisher changes )
    {
        this( new McpServerFactory( repository, changes ) );
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
