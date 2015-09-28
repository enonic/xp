package com.enonic.xp.lib.auth;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.session.SessionKey;
import com.enonic.xp.session.SimpleSession;
import com.enonic.xp.testing.script.ScriptTestSupport;

public class GetUserHandlerTest
    extends ScriptTestSupport
{
    private static final Instant NOW = Instant.ofEpochSecond( 0 );

    private static Clock clock = Clock.fixed( NOW, ZoneId.of( "UTC" ) );

    private SimpleSession session;

    @Before
    public void setup()
    {
        this.session = new SimpleSession( SessionKey.generate() );
        ContextAccessor.current().getLocalScope().setSession( session );
    }

    @Test
    public void testGetUserAuthenticated()
        throws Exception
    {
        final User user = User.create().
            key( PrincipalKey.ofUser( UserStoreKey.from( "enonic" ), "user1" ) ).
            displayName( "User 1" ).
            modifiedTime( Instant.now( clock ) ).
            email( "user1@enonic.com" ).
            login( "user1" ).
            build();
        final AuthenticationInfo authInfo = AuthenticationInfo.create().user( user ).principals( RoleKeys.ADMIN_LOGIN ).build();

        this.session.setAttribute( authInfo );

        runTestFunction( "/test/getUser-test.js", "getUserAuthenticated" );
    }

    @Test
    public void testGetUserNotAuthenticated()
        throws Exception
    {
        final AuthenticationInfo authInfo = AuthenticationInfo.unAuthenticated();
        this.session.setAttribute( authInfo );

        runTestFunction( "/test/getUser-test.js", "getUserNotAuthenticated" );
    }
}
