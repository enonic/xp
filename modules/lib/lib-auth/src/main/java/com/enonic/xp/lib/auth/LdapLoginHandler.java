package com.enonic.xp.lib.auth;

import java.util.Hashtable;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.CreateUserParams;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityConstants;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.User;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.security.auth.UsernamePasswordAuthToken;

public class LdapLoginHandler
    implements ScriptBean
{
    private final static Logger LOG = LoggerFactory.getLogger( LdapLoginHandler.class );

    private String user;

    private String password;

    private String ldapAddress;

    private UserStoreKey userStore;

    private Supplier<SecurityService> securityService;

    private Supplier<com.enonic.xp.context.Context> context;

    public void setUser( final String user )
    {
        this.user = user;
    }

    public void setPassword( final String password )
    {
        this.password = password;
    }

    public void setLdapAddress( final String ldapAddress )
    {
        this.ldapAddress = ldapAddress;
    }

    public void setUserStore( final String userStore )
    {
        this.userStore = UserStoreKey.from( userStore );
    }

    public LoginResultMapper login()
    {
        if ( isAuthenticatedInLdap() )
        {

            if ( !userExists() )
            {
                createUser();
            }

            return new LoginResultMapper( authenticate() );

        }

        return new LoginResultMapper( AuthenticationInfo.unAuthenticated() );
    }

    private boolean isAuthenticatedInLdap()
    {
        try
        {
            DirContext ctx = new InitialDirContext( buildLdapProperties() );

            boolean result = ctx != null;

            if ( ctx != null )
            {
                ctx.close();
            }

            return result;

        }
        catch ( Exception e )
        {
            LOG.error( "Problem occured while authenticating: ", e );
            return false;
        }
    }

    private Hashtable<String, String> buildLdapProperties()
    {
        final Hashtable<String, String> env = new Hashtable<>();
        env.put( Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory" );
        env.put( Context.PROVIDER_URL, "ldap://" + ldapAddress + ":389" );
        env.put( Context.SECURITY_AUTHENTICATION, "simple" );
        env.put( Context.SECURITY_PRINCIPAL, "uid=" + user + ",ou=People,dc=maxcrc,dc=com" );
        env.put( Context.SECURITY_CREDENTIALS, password );

        return env;
    }

    private boolean userExists()
    {
        final Optional<User> appUser =
            runAsAuthenticated( () -> this.securityService.get().getUser( PrincipalKey.ofUser( userStore, user ) ) );
        return appUser.isPresent();
    }

    private void createUser()
    {
        final CreateUserParams createUserParams = CreateUserParams.create().displayName( user ).login( user ).password( password ).userKey(
            PrincipalKey.ofUser( userStore, user ) ).build();

        runAsAdmin( () -> this.securityService.get().createUser( createUserParams ) );
    }

    private AuthenticationInfo authenticate()
    {
        final UsernamePasswordAuthToken usernameAuthToken = new UsernamePasswordAuthToken();
        usernameAuthToken.setUsername( user );
        usernameAuthToken.setPassword( password );
        usernameAuthToken.setUserStore( userStore );

        final AuthenticationInfo authInfo = runAsAuthenticated( () -> this.securityService.get().authenticate( usernameAuthToken ) );
        return authInfo;
    }

    private <T> T runAsAuthenticated( Callable<T> runnable )
    {
        final AuthenticationInfo authInfo = AuthenticationInfo.create().principals( RoleKeys.AUTHENTICATED ).user( User.ANONYMOUS ).build();
        return ContextBuilder.from( this.context.get() ).
            authInfo( authInfo ).
            repositoryId( SecurityConstants.SECURITY_REPO.getId() ).
            branch( SecurityConstants.BRANCH_SECURITY ).build().
            callWith( runnable );
    }

    private <T> T runAsAdmin( final Callable<T> runnable )
    {
        final User admin = User.create().key( PrincipalKey.ofUser( UserStoreKey.system(), "su" ) ).login( "su" ).build();
        final AuthenticationInfo authInfo = AuthenticationInfo.create().principals( RoleKeys.ADMIN ).user( admin ).build();

        return ContextBuilder.from( this.context.get() ).
            authInfo( authInfo ).
            repositoryId( SecurityConstants.SECURITY_REPO.getId() ).
            branch( SecurityConstants.BRANCH_SECURITY ).build().
            callWith( runnable );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.securityService = context.getService( SecurityService.class );
        this.context = context.getBinding( com.enonic.xp.context.Context.class );
    }

}
