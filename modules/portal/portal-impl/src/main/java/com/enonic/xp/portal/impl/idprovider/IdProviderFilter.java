package com.enonic.xp.portal.impl.idprovider;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

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

        // handle401 (interactive login) exists on the web connector only.
        final HttpServletResponse response =
            DispatchConstants.XP_CONNECTOR.equals( req.getAttribute( DispatchConstants.CONNECTOR_ATTRIBUTE ) )
                ? new IdProviderResponseWrapper( idProviderControllerService, req, res )
                : res;

        if ( !ContextAccessor.current().getAuthInfo().isAuthenticated() )
        {
            final IdProviderKey defaultKey = virtualHost.getDefaultIdProviderKey();
            final List<IdProviderKey> idProviderKeys =
                Stream.concat( Stream.ofNullable( defaultKey ), virtualHost.getIdProviderKeys()
                    .stream()
                    .filter( key -> !key.equals( defaultKey ) ) )
                    .filter( key -> {
                        final Set<String> flows = virtualHost.getIdProviderFlows( key );
                        return flows.isEmpty() || flows.contains( IdProviderFlow.AUTOLOGIN );
                    } )
                    .toList();

            for ( final IdProviderKey idProviderKey : idProviderKeys )
            {
                final PortalResponse portalResponse = idProviderControllerService.execute( IdProviderControllerExecutionParams.create()
                                                                                               .functionName( "autoLogin" )
                                                                                               .idProviderKey( idProviderKey )
                                                                                               .servletRequest( req )
                                                                                               .build() );
                // Id providers may authenticate without returning a response.
                if ( portalResponse != null || ContextAccessor.current().getAuthInfo().isAuthenticated() )
                {
                    break;
                }
            }
        }

        final PrincipalKeys allow = virtualHost.getAllowedPrincipals();
        if ( !allow.isEmpty() )
        {
            // The id provider endpoints are exempt, otherwise the allow list locks everyone out of
            // interactive login. They live at exactly one location: right on the vhost target.
            final String idProviderEndpoints =
                ( "/".equals( virtualHost.getTarget() ) ? "" : virtualHost.getTarget() ) + "/_/idprovider/";
            final String path = req.getPathInfo();
            if ( path == null || !path.startsWith( idProviderEndpoints ) )
            {
                final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
                if ( !authInfo.isAuthenticated() )
                {
                    response.sendError( HttpServletResponse.SC_UNAUTHORIZED );
                    return;
                }
                if ( authInfo.getPrincipals().stream().noneMatch( allow::contains ) )
                {
                    response.sendError( HttpServletResponse.SC_FORBIDDEN );
                    return;
                }
            }
        }

        chain.doFilter( new IdProviderRequestWrapper( req ), response );
    }
}
