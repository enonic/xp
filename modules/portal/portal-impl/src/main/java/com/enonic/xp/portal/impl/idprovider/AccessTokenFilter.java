package com.enonic.xp.portal.impl.idprovider;

import java.util.Optional;
import java.util.Set;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.annotation.Order;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.security.auth.VerifiedUserKeyAuthToken;
import com.enonic.xp.security.token.AccessTokenClaims;
import com.enonic.xp.security.token.AccessTokenService;
import com.enonic.xp.security.token.AccessTokenVerification;
import com.enonic.xp.security.token.Grant;
import com.enonic.xp.security.token.GrantService;
import com.enonic.xp.security.token.VerifyAccessTokenParams;
import com.enonic.xp.web.filter.OncePerRequestFilter;
import com.enonic.xp.web.vhost.IdProviderFlow;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;
import com.enonic.xp.web.vhost.VirtualHostIdProvider;

/**
 * Accepts XP's own access tokens as bearer tokens, before the id providers' auto-login. Bearer
 * tokens of other issuers are left to the id provider's {@code autoLogin}.
 * <p>
 * A token is accepted on a vhost if the user's id provider is enabled there with the
 * {@link IdProviderFlow#AUTOLOGIN autologin} flow (or no flow restriction), the vhost accepts its
 * audience, and its grant is live. The request then runs as the user with the grant's current
 * restriction, and the token is available to controllers as the request's access token.
 * <p>
 * An XP token that fails a check gets 401 with {@code WWW-Authenticate: Bearer error="invalid_token"},
 * written before the id provider filter runs, so a client with an expired or revoked token gets
 * the header and not a login page.
 */
@Component(immediate = true, service = Filter.class, property = {"connector=xp", "connector=api", "connector=status"})
@Order(-31)
@WebFilter("/*")
public final class AccessTokenFilter
    extends OncePerRequestFilter
{
    /**
     * The request attribute holding the verified {@link AccessTokenClaims} of a token-authenticated request.
     */
    public static final String CLAIMS_ATTRIBUTE = AccessTokenClaims.class.getName();

    private static final String BEARER_PREFIX = "Bearer ";

    private static final Logger LOG = LoggerFactory.getLogger( AccessTokenFilter.class );

    private final AccessTokenService accessTokenService;

    private final GrantService grantService;

    private final SecurityService securityService;

    @Activate
    public AccessTokenFilter( @Reference final AccessTokenService accessTokenService, @Reference final GrantService grantService,
                              @Reference final SecurityService securityService )
    {
        this.accessTokenService = accessTokenService;
        this.grantService = grantService;
        this.securityService = securityService;
    }

    @Override
    protected void doHandle( final HttpServletRequest req, final HttpServletResponse res, final FilterChain chain )
        throws Exception
    {
        if ( ContextAccessor.current().getAuthInfo().isAuthenticated() )
        {
            chain.doFilter( req, res );
            return;
        }
        final String header = req.getHeader( "Authorization" );
        if ( header == null || !header.regionMatches( true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length() ) )
        {
            chain.doFilter( req, res );
            return;
        }
        final String token = header.substring( BEARER_PREFIX.length() ).trim();
        switch ( accessTokenService.verify( VerifyAccessTokenParams.create().token( token ).build() ) )
        {
            case AccessTokenVerification.InvalidIssuer _ -> chain.doFilter( req, res );
            case AccessTokenVerification.Invalid _ -> reject( res, "The access token is invalid or expired" );
            case AccessTokenVerification.Verified( AccessTokenClaims claims ) -> accept( claims, req, res, chain );
        }
    }

    private void accept( final AccessTokenClaims claims, final HttpServletRequest req, final HttpServletResponse res,
                         final FilterChain chain )
        throws Exception
    {
        final VirtualHost virtualHost = VirtualHostHelper.getVirtualHost( req );
        final String refusal = virtualHost == null ? "No virtual host" : refusal( claims, virtualHost );
        if ( refusal != null )
        {
            LOG.debug( "Token [{}] not accepted on vhost [{}]: {}", claims.getTokenId(), virtualHost == null ? null : virtualHost.getName(),
                       refusal );
            reject( res, "The access token is not accepted here" );
            return;
        }
        final Optional<Grant> grant =
            claims.getGrantId() == null ? Optional.empty() : grantService.get( claims.getSubject(), claims.getGrantId() );
        if ( grant.isEmpty() )
        {
            LOG.debug( "Token [{}] rejected: no live grant [{}]", claims.getTokenId(), claims.getGrantId() );
            reject( res, "The access token has been revoked" );
            return;
        }

        final AuthenticationInfo authInfo =
            securityService.authenticate( new VerifiedUserKeyAuthToken( claims.getSubject(), grant.get().getRestriction() ) );
        if ( !authInfo.isAuthenticated() )
        {
            LOG.debug( "Token [{}] rejected: user [{}] cannot be authenticated", claims.getTokenId(), claims.getSubject() );
            reject( res, "The access token is invalid or expired" );
            return;
        }

        ContextAccessor.current().getLocalScope().setAttribute(
            claims.getActor() == null ? authInfo : AuthenticationInfo.copyOf( authInfo ).actor( claims.getActor() ).build() );
        req.setAttribute( CLAIMS_ATTRIBUTE, claims );
        chain.doFilter( req, res );
    }

    /**
     * Why the vhost refuses the token, or null if it accepts it.
     */
    static String refusal( final AccessTokenClaims claims, final VirtualHost virtualHost )
    {
        final IdProviderKey idProvider = claims.getSubject().getIdProviderKey();
        final VirtualHostIdProvider vhostIdProvider = virtualHost.getIdProviders().get( idProvider );
        if ( vhostIdProvider == null )
        {
            return "id provider [" + idProvider + "] is not enabled";
        }
        final Set<String> flows = vhostIdProvider.getFlows();
        if ( !flows.isEmpty() && !flows.contains( IdProviderFlow.AUTOLOGIN ) )
        {
            return "the autologin flow is disabled for id provider [" + idProvider + "]";
        }
        // A verified token's issuer is XP's own, which is what a vhost declaring no audience expects.
        if ( virtualHost.acceptsAudience( claims.getAudience(), claims.getIssuer() ) )
        {
            return null;
        }
        final Set<String> served = virtualHost.getAudience();
        return served.isEmpty()
            ? "audience " + claims.getAudience() + " does not name this XP [" + claims.getIssuer() + "], vhost declares no audience"
            : "audience " + claims.getAudience() + " is not served, vhost serves " + served;
    }

    private static void reject( final HttpServletResponse res, final String description )
        throws java.io.IOException
    {
        res.setHeader( "WWW-Authenticate", "Bearer error=\"invalid_token\", error_description=\"" + description + "\"" );
        res.sendError( HttpServletResponse.SC_UNAUTHORIZED );
    }
}
