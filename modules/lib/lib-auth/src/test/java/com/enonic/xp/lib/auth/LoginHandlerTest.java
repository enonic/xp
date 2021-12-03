package com.enonic.xp.lib.auth;

import java.util.ArrayList;
import java.util.List;

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

        ContextAccessor.current().getLocalScope().setSession( new SessionMock() );
    }

    @AfterEach
    public void removeNoSessionAuthInfo()
    {
        ContextAccessor.current().getLocalScope().removeAttribute( AuthenticationInfo.class );
    }

    @Test
    public void testExamples()
    {
        final AuthenticationInfo authInfo = TestDataFixtures.createAuthenticationInfo();

        final IdProviders idProviders =
            IdProviders.from( IdProvider.create().displayName( "system" ).key( IdProviderKey.from( "system" ) ).build() );

        Mockito.when( this.securityService.authenticate( Mockito.any() ) ).thenReturn( authInfo );
        Mockito.when( this.securityService.getIdProviders() ).thenReturn( idProviders );

        runScript( "/lib/xp/examples/auth/login.js" );
    }

    @Test
    public void testLoginSuccess()
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
    public void testLoginWithScopeNONE()
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
    public void testLoginSuccessNoSession()
    {
        final AuthenticationInfo authInfo =
            AuthenticationInfo.create().user( TestDataFixtures.getTestUser() ).principals( RoleKeys.ADMIN_LOGIN ).build();

        Mockito.when( this.securityService.authenticate( Mockito.any() ) ).thenReturn( authInfo );

        runFunction( "/test/login-test.js", "loginSuccessNoSession" );

        final AuthenticationInfo localScopeAuth = ContextAccessor.current().getLocalScope().getAttribute( AuthenticationInfo.class );
        final Session session = ContextAccessor.current().getLocalScope().getSession();
        final AuthenticationInfo sessionAuthInfo = session.getAttribute( AuthenticationInfo.class );
        assertEquals( authInfo, localScopeAuth );
        assertEquals( null, sessionAuthInfo );
    }

    @Test
    public void testLoginNoIdProviders()
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
    public void testLoginMultipleIdProvider()
    {
        final AuthenticationInfo authInfo = TestDataFixtures.createAuthenticationInfo();

        Mockito.when( this.securityService.authenticate( Mockito.any() ) ).thenReturn( authInfo );

        runFunction( "/test/login-test.js", "loginMultipleIdProvider" );

        final Session session = ContextAccessor.current().getLocalScope().getSession();
        final AuthenticationInfo sessionAuthInfo = session.getAttribute( AuthenticationInfo.class );
        assertEquals( authInfo, sessionAuthInfo );
    }

    @Test
    public void testInvalidLogin()
    {
        final AuthenticationInfo authInfo = AuthenticationInfo.unAuthenticated();

        Mockito.when( this.securityService.authenticate( Mockito.any() ) ).thenReturn( authInfo );

        runFunction( "/test/login-test.js", "invalidLogin" );

        final Session session = ContextAccessor.current().getLocalScope().getSession();
        final AuthenticationInfo sessionAuthInfo = session.getAttribute( AuthenticationInfo.class );
        assertNull( sessionAuthInfo );
    }

    @Test
    public void testLoginMultipleIdProvidersInOrder()
    {
        final IdProvider idProvider1 =
            IdProvider.create().displayName( "Id Provider 1" ).key( IdProviderKey.from( "idprovider1" ) ).build();
        final IdProvider idProvider3 =
            IdProvider.create().displayName( "Id Provider 3" ).key( IdProviderKey.from( "idprovider3" ) ).build();
        final IdProvider idProvider2 =
            IdProvider.create().displayName( "Id Provider 2" ).key( IdProviderKey.from( "idprovider2" ) ).build();
        final IdProviders idProviders = IdProviders.from( idProvider1, idProvider3, idProvider2 );

        final AuthenticationInfo authInfo = TestDataFixtures.createAuthenticationInfo();

        final EmailPasswordAuthToken expectedAuthToken = new EmailPasswordAuthToken();
        expectedAuthToken.setEmail( "user1@enonic.com" );
        expectedAuthToken.setPassword( "pwd123" );
        expectedAuthToken.setIdProvider( idProvider3.getKey() );

        final AuthTokenMatcher matcher = new AuthTokenMatcher( expectedAuthToken );
        Mockito.when( this.securityService.authenticate( Mockito.argThat( matcher ) ) ).thenReturn( authInfo );
        Mockito.when( this.securityService.getIdProviders() ).thenReturn( idProviders );

        runFunction( "/test/login-test.js", "loginMultipleIdProvidersInOrder" );

        final Session session = ContextAccessor.current().getLocalScope().getSession();
        final AuthenticationInfo sessionAuthInfo = session.getAttribute( AuthenticationInfo.class );
        assertEquals( authInfo, sessionAuthInfo );
        assertEquals( 3, matcher.loginIdProviderAttempts.size() );
        assertEquals( "idprovider1", matcher.loginIdProviderAttempts.get( 0 ).toString() );
        assertEquals( "idprovider2", matcher.loginIdProviderAttempts.get( 1 ).toString() );
        assertEquals( "idprovider3", matcher.loginIdProviderAttempts.get( 2 ).toString() );
    }

    @Test
    public void testSessionInvalidatedOnLogin()
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

        List<IdProviderKey> loginIdProviderAttempts = new ArrayList<>();

        @Override
        public boolean matches( AuthenticationToken argument )
        {
            if ( !( argument instanceof EmailPasswordAuthToken ) )
            {
                return false;
            }

            final EmailPasswordAuthToken authToken = (EmailPasswordAuthToken) argument;
            loginIdProviderAttempts.add( authToken.getIdProvider() );

            return thisObject.getClass().equals( authToken.getClass() ) &&
                this.thisObject.getIdProvider().equals( authToken.getIdProvider() ) &&
                this.thisObject.getEmail().equals( authToken.getEmail() ) &&
                this.thisObject.getPassword().equals( authToken.getPassword() );
        }
    }
}
