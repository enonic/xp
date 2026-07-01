package com.enonic.xp.portal.impl.idprovider;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.annotation.Order;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.portal.idprovider.IdProviderControllerExecutionParams;
import com.enonic.xp.portal.idprovider.IdProviderControllerService;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.web.dispatch.DispatchConstants;
import com.enonic.xp.web.filter.OncePerRequestFilter;
import com.enonic.xp.web.vhost.IdProviderFlow;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

@Component(immediate = true, service = Filter.class, property = {"connector=xp", "connector=api"})
@Order(-30)
@WebFilter("/*")
public final class IdProviderFilter
    extends OncePerRequestFilter
{
    private final IdProviderControllerService idProviderControllerService;

    @Activate
    public IdProviderFilter( @Reference final IdProviderControllerService idProviderControllerService )
    {
        this.idProviderControllerService = idProviderControllerService;
    }

    @Override
    protected void doHandle( final HttpServletRequest req, final HttpServletResponse res, final FilterChain chain )
        throws Exception
    {
        // If the current user is not authenticated
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        if ( !authInfo.isAuthenticated() )
        {
            // Executes the autoLogin function of the first id provider (default first) that has the
            // autologin flow enabled on this vhost.
            final IdProviderKey idProviderKey = resolveAutoLoginIdProvider( req );
            if ( idProviderKey != null )
            {
                idProviderControllerService.execute( IdProviderControllerExecutionParams.create()
                                                         .functionName( "autoLogin" )
                                                         .idProviderKey( idProviderKey )
                                                         .servletRequest( req )
                                                         .build() );
            }
        }

        //Wraps the response to handle 403 errors
        final HttpServletResponse response = DispatchConstants.XP_CONNECTOR.equals( req.getAttribute( DispatchConstants.CONNECTOR_ATTRIBUTE ) )
            ? new IdProviderResponseWrapper( idProviderControllerService, req, res )
            : res;

        final IdProviderRequestWrapper requestWrapper = new IdProviderRequestWrapper( req );

        chain.doFilter( requestWrapper, response );
    }

    private static IdProviderKey resolveAutoLoginIdProvider( final HttpServletRequest req )
    {
        final VirtualHost virtualHost = VirtualHostHelper.getVirtualHost( req );
        if ( virtualHost == null )
        {
            return null;
        }

        final IdProviderKey defaultKey = virtualHost.getDefaultIdProviderKey();
        if ( defaultKey != null && virtualHost.getIdProviderFlows( defaultKey ).contains( IdProviderFlow.AUTOLOGIN ) )
        {
            return defaultKey;
        }

        for ( final IdProviderKey key : virtualHost.getIdProviderKeys() )
        {
            if ( virtualHost.getIdProviderFlows( key ).contains( IdProviderFlow.AUTOLOGIN ) )
            {
                return key;
            }
        }
        return null;
    }
}
