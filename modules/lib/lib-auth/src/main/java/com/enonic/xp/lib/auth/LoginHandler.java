package com.enonic.xp.lib.auth;

import java.util.function.Supplier;

import jakarta.servlet.http.HttpSession;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.LocalScope;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.security.auth.AuthenticationToken;
import com.enonic.xp.security.auth.EmailPasswordAuthToken;
import com.enonic.xp.security.auth.UsernamePasswordAuthToken;
import com.enonic.xp.security.auth.VerifiedEmailAuthToken;
import com.enonic.xp.security.auth.VerifiedUsernameAuthToken;
import com.enonic.xp.session.Session;

public final class LoginHandler
    implements ScriptBean
{
    private String user;

    private String password;

    private boolean skipAuth;

    private String idProvider;

    private Integer sessionTimeout;

    private Scope scope;

    private Supplier<SecurityService> securityServiceSupplier;

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

    public LoginResultMapper login()
    {
        AuthenticationInfo authInfo = authenticate();

        if ( authInfo.isAuthenticated() )
        {
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

    private AuthenticationInfo authenticate()
    {
        final IdProviderKey idp = idProvider == null ? IdProviderKey.system() : IdProviderKey.from( idProvider );

        AuthenticationInfo authInfo = AuthenticationInfo.unAuthenticated();

        if ( isValidEmail( this.user ) )
        {
            final AuthenticationToken authToken =
                this.skipAuth ? new VerifiedEmailAuthToken( idp, this.user ) : new EmailPasswordAuthToken( idp, this.user, this.password );
            authInfo = this.securityServiceSupplier.get().authenticate( authToken );
        }

        if ( !authInfo.isAuthenticated() )
        {
            final AuthenticationToken authToken;
            if ( this.skipAuth )
            {
                authToken = new VerifiedUsernameAuthToken( idp, this.user );
            }
            else
            {
                authToken = new UsernamePasswordAuthToken( idp, this.user, this.password );
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
        this.context = context.getBinding( Context.class );
        this.portalRequestSupplier = context.getBinding( PortalRequest.class );
    }

    private enum Scope
    {
        SESSION, REQUEST, NONE
    }
}
