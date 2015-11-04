package com.enonic.xp.lib.auth;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.UserStore;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.UserStores;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.session.Session;
import com.enonic.xp.session.SessionKey;
import com.enonic.xp.session.SimpleSession;
import com.enonic.xp.testing.script.ScriptTestSupport;

public class LoginHandlerTest
    extends ScriptTestSupport
{
    private static final Instant NOW = Instant.ofEpochSecond( 0 );

    private static Clock clock = Clock.fixed( NOW, ZoneId.of( "UTC" ) );

    private SecurityService securityService;

    @Before
    public void setup()
    {
        this.securityService = Mockito.mock( SecurityService.class );
        addService( SecurityService.class, this.securityService );

        final SimpleSession session = new SimpleSession( SessionKey.generate() );
        ContextAccessor.current().getLocalScope().setSession( session );
    }

    @Test
    public void testLoginSuccess()
        throws Exception
    {
        final AuthenticationInfo authInfo =
            AuthenticationInfo.create().user( HandlerTestHelper.getTestUser() ).principals( RoleKeys.ADMIN_LOGIN ).build();

        Mockito.when( this.securityService.authenticate( Mockito.any() ) ).thenReturn( authInfo );

        runTestFunction( "/test/login-test.js", "loginSuccess" );

        final Session session = ContextAccessor.current().getLocalScope().getSession();
        final AuthenticationInfo sessionAuthInfo = session.getAttribute( AuthenticationInfo.class );
        Assert.assertEquals( authInfo, sessionAuthInfo );
    }

    @Test
    public void testLoginNoUserStore()
        throws Exception
    {

        final UserStores userStores =
            UserStores.from( UserStore.create().displayName( "system" ).key( UserStoreKey.from( "system" ) ).build() );

        final AuthenticationInfo authInfo = HandlerTestHelper.createAuthenticationInfo();

        Mockito.when( this.securityService.authenticate( Mockito.any() ) ).thenReturn( authInfo );
        Mockito.when( this.securityService.getUserStores() ).thenReturn( userStores );

        runTestFunction( "/test/login-test.js", "loginNoUserStore" );

        final Session session = ContextAccessor.current().getLocalScope().getSession();
        final AuthenticationInfo sessionAuthInfo = session.getAttribute( AuthenticationInfo.class );
        Assert.assertEquals( authInfo, sessionAuthInfo );
    }

    @Test
    public void testLoginMultipleUserStore()
        throws Exception
    {

        final AuthenticationInfo authInfo = HandlerTestHelper.createAuthenticationInfo();

        Mockito.when( this.securityService.authenticate( Mockito.any() ) ).thenReturn( authInfo );

        runTestFunction( "/test/login-test.js", "loginMultipleUserStore" );

        final Session session = ContextAccessor.current().getLocalScope().getSession();
        final AuthenticationInfo sessionAuthInfo = session.getAttribute( AuthenticationInfo.class );
        Assert.assertEquals( authInfo, sessionAuthInfo );
    }

    @Test
    public void testInvalidLogin()
        throws Exception
    {
        final AuthenticationInfo authInfo = AuthenticationInfo.unAuthenticated();

        Mockito.when( this.securityService.authenticate( Mockito.any() ) ).thenReturn( authInfo );

        runTestFunction( "/test/login-test.js", "invalidLogin" );

        final Session session = ContextAccessor.current().getLocalScope().getSession();
        final AuthenticationInfo sessionAuthInfo = session.getAttribute( AuthenticationInfo.class );
        Assert.assertNull( sessionAuthInfo );
    }
}
