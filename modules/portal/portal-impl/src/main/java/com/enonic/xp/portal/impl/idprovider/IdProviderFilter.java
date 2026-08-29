package com.enonic.xp.portal.impl.idprovider;

import java.util.ArrayList;
import java.util.List;

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
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.idprovider.IdProviderControllerExecutionParams;
import com.enonic.xp.portal.idprovider.IdProviderControllerService;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.web.dispatch.DispatchConstants;
import com.enonic.xp.web.filter.OncePerRequestFilter;
import com.enonic.xp.web.vhost.IdProviderFlow;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

@Component(immediate = true, service = Filter.class, property = {"connector=xp", "connector=api", "connector=status"})
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
        final VirtualHost virtualHost = VirtualHostHelper.getVirtualHost( req );

        // If the current user is not authenticated
        if ( !ContextAccessor.current().getAuthInfo().isAuthenticated() )
        {
            if ( virtualHost == null )
            {
                idProviderControllerService.execute(
                    IdProviderControllerExecutionParams.create().functionName( "autoLogin" ).servletRequest( req ).build() );
            }
            else
            {
                // Executes the autoLogin function of the first id provider (default first) that
                // implements it and does not have the autologin flow disabled on this vhost.
                // A single execute call both discovers and runs the function: it returns null
                // without side effects when the id provider does not implement autoLogin, letting
                // the next id provider take control.
                for ( final IdProviderKey idProviderKey : autoLoginIdProviders( virtualHost ) )
                {
                    final PortalResponse portalResponse = idProviderControllerService.execute( IdProviderControllerExecutionParams.create()
                                                                                                   .functionName( "autoLogin" )
                                                                                                   .idProviderKey( idProviderKey )
                                                                                                   .servletRequest( req )
                                                                                                   .build() );
                    if ( portalResponse != null )
                    {
                        break;
                    }
                }
            }
        }

        // Interactive login (handle401) only exists on the web connector; the management and
        // statistics ports support the non-interactive flows only.
        final HttpServletResponse response = DispatchConstants.XP_CONNECTOR.equals( req.getAttribute( DispatchConstants.CONNECTOR_ATTRIBUTE ) )
            ? new IdProviderResponseWrapper( idProviderControllerService, req, res )
            : res;

        // The vhost's principals allow list is checked before requests pass through. On the web
        // connector the wrapped response lets the default id provider's login flow (when enabled)
        // render the 401.
        final PrincipalKeys allowedPrincipals = virtualHost == null ? PrincipalKeys.empty() : virtualHost.getAllowedPrincipals();
        if ( !allowedPrincipals.isEmpty() && !isIdProviderEndpoint( req.getPathInfo() ) )
        {
            final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
            if ( !authInfo.isAuthenticated() )
            {
                response.sendError( HttpServletResponse.SC_UNAUTHORIZED );
                return;
            }
            if ( authInfo.getPrincipals().stream().noneMatch( allowedPrincipals::contains ) )
            {
                response.sendError( HttpServletResponse.SC_FORBIDDEN );
                return;
            }
        }

        final IdProviderRequestWrapper requestWrapper = new IdProviderRequestWrapper( req );

        chain.doFilter( requestWrapper, response );
    }

    private static List<IdProviderKey> autoLoginIdProviders( final VirtualHost virtualHost )
    {
        final List<IdProviderKey> result = new ArrayList<>();

        final IdProviderKey defaultKey = virtualHost.getDefaultIdProviderKey();
        if ( defaultKey != null && virtualHost.getIdProviderFlows( defaultKey ).contains( IdProviderFlow.AUTOLOGIN ) )
        {
            result.add( defaultKey );
        }

        for ( final IdProviderKey key : virtualHost.getIdProviderKeys() )
        {
            if ( !key.equals( defaultKey ) && virtualHost.getIdProviderFlows( key ).contains( IdProviderFlow.AUTOLOGIN ) )
            {
                result.add( key );
            }
        }
        return result;
    }

    // The id provider endpoints (login page, login/logout functions, callbacks, static assets) must
    // stay reachable for unauthenticated users, otherwise the allow list locks everyone out.
    private static boolean isIdProviderEndpoint( final String path )
    {
        return path != null && path.contains( "/_/idprovider/" );
    }
}
