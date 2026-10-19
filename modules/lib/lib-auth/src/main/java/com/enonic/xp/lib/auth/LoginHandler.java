package com.enonic.xp.lib.auth;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import jakarta.servlet.http.HttpSession;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.LocalScope;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleRestriction;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.auth.AccessTokenInfo;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.security.auth.AuthenticationToken;
import com.enonic.xp.security.auth.EmailPasswordAuthToken;
import com.enonic.xp.security.auth.UsernamePasswordAuthToken;
import com.enonic.xp.security.auth.VerifiedEmailAuthToken;
import com.enonic.xp.security.auth.VerifiedUsernameAuthToken;
import com.enonic.xp.security.token.AccessTokenService;
import com.enonic.xp.session.Session;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostIdProvider;

public final class LoginHandler
    implements ScriptBean
{
    private String user;

    private String password;

    private boolean skipAuth;

    private String idProvider;

    private Integer sessionTimeout;

    private Scope scope;

    private RoleRestriction restriction = RoleRestriction.none();

    private PrincipalKey actor;

    private String tokenIssuer;

    private String tokenClientId;

    private Set<String> tokenScope = Set.of();

    private Set<String> tokenAudience = Set.of();

    private Supplier<SecurityService> securityServiceSupplier;

    private Supplier<AccessTokenService> accessTokenServiceSupplier;

    private Supplier<Context> context;

    private Supplier<PortalRequest> portalRequestSupplier;

    public void setUser( final String user )
    {
        this.user = user;
    }

    public void setPassword( final String password )
    {
        this.password = password;
    }

    public void setSkipAuth( final boolean skipAuth )
    {
        this.skipAuth = skipAuth;
    }

    public void setIdProvider( final String idProvider )
    {
        this.idProvider = idProvider;
    }

    public void setSessionTimeout( final Integer sessionTimeout )
    {
        this.sessionTimeout = sessionTimeout;
    }

    public void setScope( final String scope )
    {
        this.scope = Scope.valueOf( scope );
    }

    public void setRoles( final String[] roles )
    {
        this.restriction = roles == null ? RoleRestriction.none() : RoleRestriction.to( PrincipalKeys.from( roles ) );
    }

    public void setActor( final String actor )
    {
        this.actor = actor == null ? null : PrincipalKey.from( actor );
    }

    public void setTokenIssuer( final String tokenIssuer )
    {
        this.tokenIssuer = tokenIssuer;
    }

    public void setTokenClientId( final String tokenClientId )
    {
        this.tokenClientId = tokenClientId;
    }

    public void setTokenScope( final String[] tokenScope )
    {
        this.tokenScope = tokenScope == null ? Set.of() : Set.copyOf( List.of( tokenScope ) );
    }

    public void setTokenAudience( final String[] tokenAudience )
    {
        this.tokenAudience = tokenAudience == null ? Set.of() : Set.copyOf( List.of( tokenAudience ) );
    }

    public LoginResultMapper login()
    {
        final IdProviderKey idp = idProvider == null ? IdProviderKey.system() : IdProviderKey.from( idProvider );

        RoleRestriction restriction = this.restriction;
        AccessTokenInfo accessToken = null;

        if ( tokenIssuer != null )
        {
            // The caller has verified the token. XP checks its audience against the host (the
            // declared list, or XP's issuer name where none is declared) and maps its scopes to
            // roles through the host's catalogue, narrowed by any explicit roles. Outside a vhost
            // the token is treated as on a host declaring no audience.
            final PortalRequest portalRequest = this.portalRequestSupplier.get();
            final VirtualHost virtualHost = portalRequest == null ? null : portalRequest.getVirtualHost();
            final String self = this.accessTokenServiceSupplier.get().getIssuer();
            final boolean audienceAccepted =
                virtualHost == null ? tokenAudience.contains( self ) : virtualHost.acceptsAudience( tokenAudience, self );
            if ( !audienceAccepted )
            {
                return new LoginResultMapper( AuthenticationInfo.unAuthenticated(), "Token audience not accepted here" );
            }
            final VirtualHostIdProvider settings = virtualHost == null ? null : virtualHost.getIdProviders().get( idp );
            restriction = restriction.narrow( settings == null ? RoleRestriction.none() : settings.restrictionFor( tokenScope ) );
            accessToken = AccessTokenInfo.create()
                .issuer( tokenIssuer )
                .clientId( tokenClientId )
                .scope( tokenScope )
                .audience( tokenAudience )
                .build();
        }

        AuthenticationInfo authInfo = authenticate( idp, restriction );

        if ( authInfo.isAuthenticated() )
        {
            if ( actor != null || accessToken != null )
            {
                final AuthenticationInfo.Builder builder = AuthenticationInfo.copyOf( authInfo );
                if ( actor != null )
                {
                    builder.actor( actor );
                }
                if ( accessToken != null )
                {
                    builder.accessToken( accessToken );
                }
                authInfo = builder.build();
            }
            switch ( this.scope )
            {
                case NONE:
                    // do nothing
                    break;
                case REQUEST:
                    this.context.get().getLocalScope().setAttribute( authInfo );
                    break;
                case SESSION:
                default:
                    createSession( authInfo );
                    break;
            }

            return new LoginResultMapper( authInfo );
        }
        else
        {
            return new LoginResultMapper( authInfo, "Access Denied" );
        }
    }

    private void createSession( final AuthenticationInfo authInfo )
    {
        final LocalScope localScope = this.context.get().getLocalScope();
        final Session session = localScope.getSession();

        if ( session != null )
        {
            final var attributes = session.getAttributes();
            session.invalidate();

            final Session newSession = localScope.getSession();

            if ( newSession != null )
            {
                attributes.forEach( newSession::setAttribute );
                session.setAttribute( authInfo );

                if ( this.sessionTimeout != null )
                {
                    setSessionTimeout();
                }
            }
        }
    }

    private AuthenticationInfo authenticate( final IdProviderKey idp, final RoleRestriction restriction )
    {
        AuthenticationInfo authInfo = AuthenticationInfo.unAuthenticated();

        if ( isValidEmail( this.user ) )
        {
            final AuthenticationToken authToken = this.skipAuth
                ? new VerifiedEmailAuthToken( idp, this.user, restriction )
                : new EmailPasswordAuthToken( idp, this.user, this.password, restriction );
            authInfo = this.securityServiceSupplier.get().authenticate( authToken );
        }

        if ( !authInfo.isAuthenticated() )
        {
            final AuthenticationToken authToken;
            if ( this.skipAuth )
            {
                authToken = new VerifiedUsernameAuthToken( idp, this.user, restriction );
            }
            else
            {
                authToken = new UsernamePasswordAuthToken( idp, this.user, this.password, restriction );
            }
            authInfo = this.securityServiceSupplier.get().authenticate( authToken );
        }

        return authInfo;
    }

    private boolean isValidEmail( final String value )
    {
        return value != null && value.chars().filter( ch -> ch == '@' ).count() == 1;
    }

    private void setSessionTimeout()
    {
        final PortalRequest portalRequest = this.portalRequestSupplier.get();
        if ( portalRequest != null )
        {
            final HttpSession httpSession = portalRequest.getRawRequest().getSession();
            if ( httpSession != null )
            {
                httpSession.setMaxInactiveInterval( this.sessionTimeout );
            }
        }
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.securityServiceSupplier = context.getService( SecurityService.class );
        this.accessTokenServiceSupplier = context.getService( AccessTokenService.class );
        this.context = context.getBinding( Context.class );
        this.portalRequestSupplier = context.getBinding( PortalRequest.class );
    }

    private enum Scope
    {
        SESSION, REQUEST, NONE
    }
}
