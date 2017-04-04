package com.enonic.xp.lib.auth;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.UserStore;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.UserStores;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.security.auth.AuthenticationToken;
import com.enonic.xp.security.auth.EmailPasswordAuthToken;
import com.enonic.xp.session.Session;
import com.enonic.xp.session.SessionKey;
import com.enonic.xp.session.SimpleSession;
import com.enonic.xp.testing.script.ScriptTestSupport;

public class LoginHandlerTest
    extends ScriptTestSupport
{
    private SecurityService securityService;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();
        this.securityService = Mockito.mock( SecurityService.class );
        addService( SecurityService.class, this.securityService );

        final SimpleSession session = new SimpleSession( SessionKey.generate() );
        ContextAccessor.current().getLocalScope().setSession( session );
    }

    @Test
    public void testExamples()
    {
        final AuthenticationInfo authInfo = TestDataFixtures.createAuthenticationInfo();

        final UserStores userStores =
            UserStores.from( UserStore.create().displayName( "system" ).key( UserStoreKey.from( "system" ) ).build() );

        Mockito.when( this.securityService.authenticate( Mockito.any() ) ).thenReturn( authInfo );
        Mockito.when( this.securityService.getUserStores() ).thenReturn( userStores );

        runScript( "/site/lib/xp/examples/auth/login.js" );
    }

    @Test
    public void testLoginSuccess()
    {
        final AuthenticationInfo authInfo =
            AuthenticationInfo.create().user( TestDataFixtures.getTestUser() ).principals( RoleKeys.ADMIN_LOGIN ).build();

        Mockito.when( this.securityService.authenticate( Mockito.any() ) ).thenReturn( authInfo );

        runFunction( "/site/test/login-test.js", "loginSuccess" );

        final Session session = ContextAccessor.current().getLocalScope().getSession();
        final AuthenticationInfo sessionAuthInfo = session.getAttribute( AuthenticationInfo.class );
        Assert.assertEquals( authInfo, sessionAuthInfo );
    }

    @Test
    public void testLoginNoUserStore()
    {
        final UserStores userStores =
            UserStores.from( UserStore.create().displayName( "system" ).key( UserStoreKey.from( "system" ) ).build() );

        final AuthenticationInfo authInfo = TestDataFixtures.createAuthenticationInfo();

        Mockito.when( this.securityService.authenticate( Mockito.any() ) ).thenReturn( authInfo );
        Mockito.when( this.securityService.getUserStores() ).thenReturn( userStores );

        runFunction( "/site/test/login-test.js", "loginNoUserStore" );

        final Session session = ContextAccessor.current().getLocalScope().getSession();
        final AuthenticationInfo sessionAuthInfo = session.getAttribute( AuthenticationInfo.class );
        Assert.assertEquals( authInfo, sessionAuthInfo );
    }

    @Test
    public void testLoginMultipleUserStore()
    {
        final AuthenticationInfo authInfo = TestDataFixtures.createAuthenticationInfo();

        Mockito.when( this.securityService.authenticate( Mockito.any() ) ).thenReturn( authInfo );

        runFunction( "/site/test/login-test.js", "loginMultipleUserStore" );

        final Session session = ContextAccessor.current().getLocalScope().getSession();
        final AuthenticationInfo sessionAuthInfo = session.getAttribute( AuthenticationInfo.class );
        Assert.assertEquals( authInfo, sessionAuthInfo );
    }

    @Test
    public void testInvalidLogin()
    {
        final AuthenticationInfo authInfo = AuthenticationInfo.unAuthenticated();

        Mockito.when( this.securityService.authenticate( Mockito.any() ) ).thenReturn( authInfo );

        runFunction( "/site/test/login-test.js", "invalidLogin" );

        final Session session = ContextAccessor.current().getLocalScope().getSession();
        final AuthenticationInfo sessionAuthInfo = session.getAttribute( AuthenticationInfo.class );
        Assert.assertNull( sessionAuthInfo );
    }

    @Test
    public void testLoginMultipleUserStoresInOrder()
    {
        final UserStore userStore1 = UserStore.create().displayName( "User Store 1" ).key( UserStoreKey.from( "userstore1" ) ).build();
        final UserStore userStore3 = UserStore.create().displayName( "User Store 3" ).key( UserStoreKey.from( "userstore3" ) ).build();
        final UserStore userStore2 = UserStore.create().displayName( "User Store 2" ).key( UserStoreKey.from( "userstore2" ) ).build();
        final UserStores userStores = UserStores.from( userStore1, userStore3, userStore2 );

        final AuthenticationInfo authInfo = TestDataFixtures.createAuthenticationInfo();

        final EmailPasswordAuthToken expectedAuthToken = new EmailPasswordAuthToken();
        expectedAuthToken.setEmail( "user1@enonic.com" );
        expectedAuthToken.setPassword( "pwd123" );
        expectedAuthToken.setUserStore( userStore3.getKey() );

        final AuthTokenMatcher matcher = new AuthTokenMatcher( expectedAuthToken );
        Mockito.when( this.securityService.authenticate( Mockito.argThat( matcher ) ) ).thenReturn( authInfo );
        Mockito.when( this.securityService.getUserStores() ).thenReturn( userStores );

        runFunction( "/site/test/login-test.js", "loginMultipleUserStoresInOrder" );

        final Session session = ContextAccessor.current().getLocalScope().getSession();
        final AuthenticationInfo sessionAuthInfo = session.getAttribute( AuthenticationInfo.class );
        Assert.assertEquals( authInfo, sessionAuthInfo );
        Assert.assertEquals( 3, matcher.loginUserStoreAttempts.size());
        Assert.assertEquals( "userstore1", matcher.loginUserStoreAttempts.get( 0 ).toString() );
        Assert.assertEquals( "userstore2", matcher.loginUserStoreAttempts.get( 1 ).toString() );
        Assert.assertEquals( "userstore3", matcher.loginUserStoreAttempts.get( 2 ).toString() );
    }

    private class AuthTokenMatcher
        extends ArgumentMatcher<AuthenticationToken>
    {
        EmailPasswordAuthToken thisObject;

        AuthTokenMatcher( EmailPasswordAuthToken thisObject )
        {
            this.thisObject = thisObject;
        }

        List<UserStoreKey> loginUserStoreAttempts = new ArrayList<>();

        @Override
        public boolean matches( Object argument )
        {
            if ( !( argument instanceof EmailPasswordAuthToken ) )
            {
                return false;
            }

            final EmailPasswordAuthToken authToken = (EmailPasswordAuthToken) argument;
            loginUserStoreAttempts.add( authToken.getUserStore() );

            return thisObject.getClass().equals( authToken.getClass() ) &&
                this.thisObject.getUserStore().equals( authToken.getUserStore() ) &&
                this.thisObject.getEmail().equals( authToken.getEmail() ) &&
                this.thisObject.getPassword().equals( authToken.getPassword() );
        }
    }
}
