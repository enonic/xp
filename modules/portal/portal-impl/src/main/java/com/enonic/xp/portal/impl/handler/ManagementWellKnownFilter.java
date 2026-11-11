package com.enonic.xp.portal.impl.handler;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.jspecify.annotations.NullMarked;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.annotation.Order;
import com.enonic.xp.portal.impl.idprovider.OAuthMetadataService;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.exception.ExceptionRenderer;
import com.enonic.xp.web.filter.OncePerRequestFilter;
import com.enonic.xp.web.serializer.WebSerializerService;
import com.enonic.xp.web.vhost.VirtualHostHelper;
import com.enonic.xp.web.vhost.WellKnownPaths;

/**
 * Serves the OAuth well-known documents on a management mapping, before authentication: a client
 * that knows only the management URL learns from them where to get a token for it.
 */
@Component(immediate = true, service = Filter.class, property = {"connector=api"})
@Order(-50)
@WebFilter("/*")
@NullMarked
public final class ManagementWellKnownFilter
    extends OncePerRequestFilter
{
    private final OAuthMetadataService metadata;

    private final WebSerializerService webSerializerService;

    private final ExceptionRenderer exceptionRenderer;

    @Activate
    public ManagementWellKnownFilter( @Reference final OAuthMetadataService metadata,
                                      @Reference final WebSerializerService webSerializerService,
                                      @Reference final ExceptionRenderer exceptionRenderer )
    {
        this.metadata = metadata;
        this.webSerializerService = webSerializerService;
        this.exceptionRenderer = exceptionRenderer;
    }

    @Override
    protected void doHandle( final HttpServletRequest req, final HttpServletResponse res, final FilterChain chain )
        throws Exception
    {
        if ( WellKnownPaths.documentOf( req.getPathInfo() ) == null || VirtualHostHelper.getVirtualHost( req ) == null ||
            !isServedMethod( req.getMethod() ) )
        {
            chain.doFilter( req, res );
            return;
        }
        final WebRequest webRequest = webSerializerService.request( req );
        WebResponse webResponse;
        try
        {
            webResponse = WellKnownDocuments.serve( metadata, webRequest );
        }
        catch ( Exception e )
        {
            webResponse = exceptionRenderer.render( webRequest, e );
        }
        webSerializerService.response( webRequest, webResponse, res );
    }

    private static boolean isServedMethod( final String method )
    {
        return WellKnownDocuments.METHODS.stream().anyMatch( served -> served.name().equals( method ) );
    }
}
