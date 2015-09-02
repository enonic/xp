package com.enonic.xp.lib.auth;

import java.util.function.Supplier;

import org.apache.commons.lang.StringUtils;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.security.auth.EmailPasswordAuthToken;
import com.enonic.xp.security.auth.UsernamePasswordAuthToken;
import com.enonic.xp.session.Session;

public final class LoginHandler
    implements ScriptBean
{
    private String user;

    private String password;

    private UserStoreKey userStore;

    private Supplier<SecurityService> securityService;

    public void setUser( final String user )
    {
        this.user = user;
    }

    public void setPassword( final String password )
    {
        this.password = password;
    }

    public void setUserStore( final String userStore )
    {
        this.userStore = UserStoreKey.from( userStore );
    }

    public LoginResultMapper login()
    {
        AuthenticationInfo authInfo = null;

        if ( isValidEmail( this.user ) )
        {
            final EmailPasswordAuthToken emailAuthToken = new EmailPasswordAuthToken();
            emailAuthToken.setEmail( this.user );
            emailAuthToken.setPassword( this.password );
            emailAuthToken.setUserStore( this.userStore );

            authInfo = this.securityService.get().authenticate( emailAuthToken );
        }
        if ( authInfo == null || !authInfo.isAuthenticated() )
        {
            final UsernamePasswordAuthToken usernameAuthToken = new UsernamePasswordAuthToken();
            usernameAuthToken.setUsername( this.user );
            usernameAuthToken.setPassword( this.password );
            usernameAuthToken.setUserStore( this.userStore );

            authInfo = this.securityService.get().authenticate( usernameAuthToken );
        }
        if ( authInfo.isAuthenticated() )
        {
            final Session session = ContextAccessor.current().getLocalScope().getSession();
            if ( session != null )
            {
                session.setAttribute( authInfo );
            }

            return new LoginResultMapper( authInfo );
        }
        else
        {
            return new LoginResultMapper( authInfo, "Access Denied" );
        }
    }

    private boolean isValidEmail( final String value )
    {
        return StringUtils.countMatches( value, "@" ) == 1;
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.securityService = context.getService( SecurityService.class );
    }
}
