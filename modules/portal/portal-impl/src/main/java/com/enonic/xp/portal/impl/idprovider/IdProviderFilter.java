package com.enonic.xp.portal.impl.idprovider;

import java.io.IOException;
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
        final HttpServletResponse response = wrapResponse( req, res );

        autoLogin( virtualHost, req );

        if ( !allow( virtualHost, req, response ) )
        {
            return;
        }

        chain.doFilter( new IdProviderRequestWrapper( req ), response );
    }

    /**
     * Interactive login (handle401) only exists on the web connector; the management and statistics
     * endpoints support the non-interactive flows only. A missing connector attribute means the web
     * connector, as everywhere else.
     */
    private HttpServletResponse wrapResponse( final HttpServletRequest req, final HttpServletResponse res )
    {
        final Object connector = req.getAttribute( DispatchConstants.CONNECTOR_ATTRIBUTE );
        return connector == null || DispatchConstants.XP_CONNECTOR.equals( connector )
            ? new IdProviderResponseWrapper( idProviderControllerService, req, res )
            : res;
    }

    /**
     * Executes the autoLogin function of the id providers that do not have the autologin flow
     * disabled on the vhost (default first). A single execute call both discovers and runs the
     * function - it returns null without side effects when the id provider does not implement
     * autoLogin - but a null response alone cannot signal control: real id providers (e.g. the
     * OIDC id provider) return nothing from autoLogin even when they authenticate. The established
     * authentication, or a returned response, is what ends the search.
     */
    private void autoLogin( final VirtualHost virtualHost, final HttpServletRequest req )
        throws IOException
    {
        if ( ContextAccessor.current().getAuthInfo().isAuthenticated() )
        {
            return;
        }

        if ( virtualHost == null )
        {
            idProviderControllerService.execute(
                IdProviderControllerExecutionParams.create().functionName( "autoLogin" ).servletRequest( req ).build() );
            return;
        }

        for ( final IdProviderKey idProviderKey : autoLoginIdProviders( virtualHost ) )
        {
            final PortalResponse portalResponse = idProviderControllerService.execute( IdProviderControllerExecutionParams.create()
                                                                                           .functionName( "autoLogin" )
                                                                                           .idProviderKey( idProviderKey )
                                                                                           .servletRequest( req )
                                                                                           .build() );
            if ( portalResponse != null || ContextAccessor.current().getAuthInfo().isAuthenticated() )
            {
                break;
            }
        }
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

    /**
     * Applies the vhost's allow list before requests pass through: unauthenticated requests are
     * rejected with 401 (on the web connector the wrapped response lets the default id provider's
     * login flow, when enabled, render it), authenticated ones without any allowed principal with
     * 403. The id provider endpoints stay reachable, otherwise the allow list locks everyone out of
     * interactive login.
     */
    private static boolean allow( final VirtualHost virtualHost, final HttpServletRequest req, final HttpServletResponse response )
        throws IOException
    {
        final PrincipalKeys allow = virtualHost == null ? PrincipalKeys.empty() : virtualHost.getAllowedPrincipals();
        if ( allow.isEmpty() || isIdProviderEndpoint( req.getPathInfo() ) )
        {
            return true;
        }

        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        if ( !authInfo.isAuthenticated() )
        {
            response.sendError( HttpServletResponse.SC_UNAUTHORIZED );
            return false;
        }
        if ( authInfo.getPrincipals().stream().noneMatch( allow::contains ) )
        {
            response.sendError( HttpServletResponse.SC_FORBIDDEN );
            return false;
        }
        return true;
    }

    // The id provider endpoints (login page, login/logout functions, callbacks, static assets).
    private static boolean isIdProviderEndpoint( final String path )
    {
        return path != null && path.contains( "/_/idprovider/" );
    }
}
