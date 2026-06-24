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
 * Authenticates a request bearing an XP-issued self-issued access token (device / native login).
 * <p>
 * Bearer authentication has two mechanisms, both enabled by the id provider's {@code autologin} flow
 * on the vhost and applied in a defined order:
 * <ol>
 *     <li><b>this filter</b> ({@link Order @Order} -31, before {@link IdProviderFilter}) goes first:
 *     it verifies the bearer as an XP-issued token (signature, {@code kid}, issuer) and, if valid,
 *     establishes the auth context from the principal + memberships. It is id-provider agnostic - an
 *     XP token is verified by XP - but is still gated by the token id provider's {@code autologin}
 *     flow on the current vhost, which is the per-vhost switch for non-interactive auth;</li>
 *     <li>otherwise the request falls through to {@link IdProviderFilter} (@Order -30), which runs
 *     the id provider's own {@code autoLogin} (e.g. validating an <i>external</i>-IdP bearer against
 *     JWKS).</li>
 * </ol>
 * So a valid XP-issued token is consumed here and never reaches the id provider's {@code autoLogin};
 * any other bearer is left untouched for it. A missing or invalid token leaves the request
 * unauthenticated and normal id-provider handling proceeds (on the {@code api} connector this never
 * triggers an interactive login).
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

        // Per-vhost flow gating: a vhost may keep the id provider configured but disable token
        // acceptance for it by not enabling the autologin flow (e.g. mapping value enabled=login).
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
