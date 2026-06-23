package com.enonic.xp.portal.impl.idprovider;

import java.util.Optional;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.annotation.Order;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.security.token.AccessToken;
import com.enonic.xp.security.token.AccessTokenService;
import com.enonic.xp.web.filter.OncePerRequestFilter;
import com.enonic.xp.web.vhost.IdProviderFlow;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

/**
 * Authenticates requests bearing a self-issued access token (e.g. a device-login token).
 * <p>
 * This runs before {@link IdProviderFilter} and is id-provider agnostic: a token issued by XP is
 * verified by XP, so it is accepted on any vhost regardless of whether the resolved principal's
 * id provider implements {@code autoLogin}. On a missing or invalid token the request is left
 * unauthenticated and normal id-provider handling proceeds (which, on the {@code api} connector,
 * never triggers an interactive login).
 */
@Component(immediate = true, service = Filter.class, property = {"connector=xp", "connector=api"})
@Order(-31)
@WebFilter("/*")
@NullMarked
public final class AccessTokenAuthFilter
    extends OncePerRequestFilter
{
    private final AccessTokenService accessTokenService;

    private final SecurityService securityService;

    @Activate
    public AccessTokenAuthFilter( @Reference final AccessTokenService accessTokenService,
                                  @Reference final SecurityService securityService )
    {
        this.accessTokenService = accessTokenService;
        this.securityService = securityService;
    }

    @Override
    protected void doHandle( final HttpServletRequest req, final HttpServletResponse res, final FilterChain chain )
        throws Exception
    {
        if ( !ContextAccessor.current().getAuthInfo().isAuthenticated() )
        {
            final AuthenticationInfo authInfo = authenticate( req );
            if ( authInfo != null )
            {
                final Context context = ContextBuilder.from( ContextAccessor.current() ).authInfo( authInfo ).build();
                final Exception[] thrown = new Exception[1];
                context.runWith( () -> {
                    try
                    {
                        chain.doFilter( req, res );
                    }
                    catch ( Exception e )
                    {
                        thrown[0] = e;
                    }
                } );
                if ( thrown[0] != null )
                {
                    throw thrown[0];
                }
                return;
            }
        }

        chain.doFilter( req, res );
    }

    @Nullable
    private AuthenticationInfo authenticate( final HttpServletRequest req )
    {
        final String token = extractBearerToken( req );
        if ( token == null )
        {
            return null;
        }

        final Optional<AccessToken> verified = accessTokenService.verify( token );
        if ( verified.isEmpty() )
        {
            return null;
        }

        final PrincipalKey subject = verified.get().getSubject();
        if ( !subject.isUser() )
        {
            return null;
        }

        // Per-vhost flow gating: a vhost may keep the id provider configured but disable
        // acceptance of device-login tokens for it (mapping.<vh>.context.<idp>.deviceLogin.accept=false).
        if ( !isAcceptanceEnabled( req, subject.getIdProviderKey() ) )
        {
            return null;
        }

        final User user = securityService.getUser( subject ).orElse( null );
        if ( user == null || user.isDisabled() )
        {
            return null;
        }

        final PrincipalKeys memberships = securityService.getAllMemberships( subject );
        return AuthenticationInfo.create()
            .principals( memberships )
            .principals( RoleKeys.AUTHENTICATED, RoleKeys.EVERYONE )
            .user( user )
            .build();
    }

    private static boolean isAcceptanceEnabled( final HttpServletRequest req, final IdProviderKey idProvider )
    {
        final VirtualHost virtualHost = VirtualHostHelper.getVirtualHost( req );
        if ( virtualHost == null )
        {
            return true;
        }
        // Token acceptance is a form of auto-login, gated per the token's own id provider.
        return virtualHost.getIdProviderFlows( idProvider ).contains( IdProviderFlow.AUTOLOGIN );
    }

    @Nullable
    private static String extractBearerToken( final HttpServletRequest req )
    {
        final String authHeader = req.getHeader( "Authorization" );
        if ( authHeader != null && authHeader.startsWith( "Bearer " ) )
        {
            return authHeader.substring( "Bearer ".length() );
        }
        return null;
    }
}
