package com.enonic.xp.lib.auth;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.security.IdProvider;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.IdProviders;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.security.auth.AuthenticationToken;
import com.enonic.xp.security.auth.EmailPasswordAuthToken;
import com.enonic.xp.session.Session;
import com.enonic.xp.session.SessionMock;
import com.enonic.xp.testing.ScriptTestSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class LoginHandlerTest
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

        ContextAccessor.current().getLocalScope().setSession( new SessionMock() );
    }

    @AfterEach
    void removeNoSessionAuthInfo()
    {
        ContextAccessor.current().getLocalScope().removeAttribute( AuthenticationInfo.class );
    }

    @Test
    void testExamples()
    {
        final AuthenticationInfo authInfo = TestDataFixtures.createAuthenticationInfo();

        final IdProviders idProviders =
            IdProviders.from( IdProvider.create().displayName( "system" ).key( IdProviderKey.from( "system" ) ).build() );

        Mockito.when( this.securityService.authenticate( Mockito.any() ) ).thenReturn( authInfo );
        Mockito.when( this.securityService.getIdProviders() ).thenReturn( idProviders );

        runScript( "/lib/xp/examples/auth/login.js" );
    }

    @Test
    void testLoginSuccess()
    {
        final AuthenticationInfo authInfo =
            AuthenticationInfo.create().user( TestDataFixtures.getTestUser() ).principals( RoleKeys.ADMIN_LOGIN ).build();

        Mockito.when( this.securityService.authenticate( Mockito.any() ) ).thenReturn( authInfo );

        runFunction( "/test/login-test.js", "loginSuccess" );

        final Session session = ContextAccessor.current().getLocalScope().getSession();
        final AuthenticationInfo sessionAuthInfo = session.getAttribute( AuthenticationInfo.class );
        assertEquals( authInfo, sessionAuthInfo );
    }

    @Test
    void testLoginWithSkipAuth()
    {
        final AuthenticationInfo authInfo =
            AuthenticationInfo.create().user( TestDataFixtures.getTestUser() ).principals( RoleKeys.ADMIN_LOGIN ).build();

        Mockito.when( this.securityService.authenticate( Mockito.any() ) ).thenReturn( authInfo );

        runFunction( "/test/login-test.js", "loginWithSkipAuth" );

        final Session session = ContextAccessor.current().getLocalScope().getSession();
        final AuthenticationInfo sessionAuthInfo = session.getAttribute( AuthenticationInfo.class );
        assertEquals( authInfo, sessionAuthInfo );
    }

    @Test
    void testLoginWithScopeNONE()
    {
        ContextAccessor.current().getLocalScope().setSession( null );

        final AuthenticationInfo authInfo =
            AuthenticationInfo.create().user( TestDataFixtures.getTestUser() ).principals( RoleKeys.ADMIN_LOGIN ).build();

        final IdProviders idProviders =
            IdProviders.from( IdProvider.create().displayName( "system" ).key( IdProviderKey.from( "system" ) ).build() );

        Mockito.when( this.securityService.authenticate( Mockito.any() ) ).thenReturn( authInfo );
        Mockito.when( this.securityService.getIdProviders() ).thenReturn( idProviders );


        runFunction( "/test/login-test.js", "loginWithScopeNONE" );

        assertNull( ContextAccessor.current().getLocalScope().getSession() );
        assertNull( ContextAccessor.current().getLocalScope().getAttribute( AuthenticationInfo.class ) );
    }

    @Test
    void testLoginSuccessNoSession()
    {
        final AuthenticationInfo authInfo =
            AuthenticationInfo.create().user( TestDataFixtures.getTestUser() ).principals( RoleKeys.ADMIN_LOGIN ).build();

        Mockito.when( this.securityService.authenticate( Mockito.any() ) ).thenReturn( authInfo );

        runFunction( "/test/login-test.js", "loginSuccessNoSession" );

        final AuthenticationInfo localScopeAuth = ContextAccessor.current().getLocalScope().getAttribute( AuthenticationInfo.class );
        final Session session = ContextAccessor.current().getLocalScope().getSession();
        final AuthenticationInfo sessionAuthInfo = session.getAttribute( AuthenticationInfo.class );
        assertEquals( authInfo, localScopeAuth );
        assertNull( sessionAuthInfo );
    }

    @Test
    void testLoginNoIdProviders()
    {
        final IdProviders idProviders =
            IdProviders.from( IdProvider.create().displayName( "system" ).key( IdProviderKey.from( "system" ) ).build() );

        final AuthenticationInfo authInfo = TestDataFixtures.createAuthenticationInfo();

        Mockito.when( this.securityService.authenticate( Mockito.any() ) ).thenReturn( authInfo );
        Mockito.when( this.securityService.getIdProviders() ).thenReturn( idProviders );

        runFunction( "/test/login-test.js", "loginNoIdProvider" );

        final Session session = ContextAccessor.current().getLocalScope().getSession();
        final AuthenticationInfo sessionAuthInfo = session.getAttribute( AuthenticationInfo.class );
        assertEquals( authInfo, sessionAuthInfo );
    }

    @Test
    void testLoginMultipleIdProvider()
    {
        final AuthenticationInfo authInfo = TestDataFixtures.createAuthenticationInfo();

        Mockito.when( this.securityService.authenticate( Mockito.any() ) ).thenReturn( authInfo );

        runFunction( "/test/login-test.js", "loginMultipleIdProvider" );

        final Session session = ContextAccessor.current().getLocalScope().getSession();
        final AuthenticationInfo sessionAuthInfo = session.getAttribute( AuthenticationInfo.class );
        assertEquals( authInfo, sessionAuthInfo );
    }

    @Test
    void testInvalidLogin()
    {
        final AuthenticationInfo authInfo = AuthenticationInfo.unAuthenticated();

        Mockito.when( this.securityService.authenticate( Mockito.any() ) ).thenReturn( authInfo );

        runFunction( "/test/login-test.js", "invalidLogin" );

        final Session session = ContextAccessor.current().getLocalScope().getSession();
        final AuthenticationInfo sessionAuthInfo = session.getAttribute( AuthenticationInfo.class );
        assertNull( sessionAuthInfo );
    }

    @Test
    void testLoginUnspecifiedIdProvider()
    {
        final AuthenticationInfo authInfo = TestDataFixtures.createAuthenticationInfo();

        final EmailPasswordAuthToken expectedAuthToken = new EmailPasswordAuthToken();
        expectedAuthToken.setEmail( "user1@enonic.com" );
        expectedAuthToken.setPassword( "pwd123" );
        expectedAuthToken.setIdProvider( null );

        final AuthTokenMatcher matcher = new AuthTokenMatcher( expectedAuthToken );
        Mockito.when( this.securityService.authenticate( Mockito.argThat( matcher ) ) ).thenReturn( authInfo );

        runFunction( "/test/login-test.js", "loginUnspecifiedIdProvider" );

        final Session session = ContextAccessor.current().getLocalScope().getSession();
        final AuthenticationInfo sessionAuthInfo = session.getAttribute( AuthenticationInfo.class );
        assertEquals( authInfo, sessionAuthInfo );
    }

    @Test
    void testSessionInvalidatedOnLogin()
    {
        final AuthenticationInfo authInfo = TestDataFixtures.createAuthenticationInfo();

        final IdProviders idProviders =
            IdProviders.from( IdProvider.create().displayName( "system" ).key( IdProviderKey.from( "system" ) ).build() );

        Mockito.when( this.securityService.authenticate( Mockito.any() ) ).thenReturn( authInfo );
        Mockito.when( this.securityService.getIdProviders() ).thenReturn( idProviders );

        final SessionMock session = Mockito.spy( new SessionMock() );
        ContextAccessor.current().getLocalScope().setSession( session );

        runScript( "/lib/xp/examples/auth/login.js" );

        verify( session, times( 5 ) ).invalidate();
    }

    private static class AuthTokenMatcher
        implements ArgumentMatcher<AuthenticationToken>
    {
        EmailPasswordAuthToken thisObject;

        AuthTokenMatcher( EmailPasswordAuthToken thisObject )
        {
            this.thisObject = thisObject;
        }

        @Override
        public boolean matches( AuthenticationToken argument )
        {
            if ( !( argument instanceof EmailPasswordAuthToken ) )
            {
                return false;
            }

            final EmailPasswordAuthToken authToken = (EmailPasswordAuthToken) argument;

            return
                this.thisObject.getIdProvider() == null &&
                this.thisObject.getEmail().equals( authToken.getEmail() ) &&
                this.thisObject.getPassword().equals( authToken.getPassword() );
        }
    }
}
