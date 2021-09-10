package com.enonic.xp.admin.impl.rest.resource.auth;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.jupiter.api.Test;

import com.enonic.xp.admin.impl.rest.resource.AdminResourceTestSupport;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.LocalScope;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.session.SessionMock;

public class AuthResourceTest
    extends AdminResourceTestSupport
{
    private static final Instant NOW = Instant.ofEpochSecond( 0 );

    private static final Clock clock = Clock.fixed( NOW, ZoneId.of( "UTC" ) );

    @Override
    protected Object getResourceInstance()
    {
        return new AuthResource();
    }

    @Test
    public void testAuthenticated_unauthenticated()
        throws Exception
    {
        String jsonString = request().path( "auth/authenticated" ).get().getAsString();

        assertJson( "authenticated_negative.json", jsonString );
    }

    @Test
    public void testAuthenticated_authenticated()
        throws Exception
    {
        final User user = User.create().
            key( PrincipalKey.ofUser( IdProviderKey.system(), "user1" ) ).
            displayName( "User 1" ).
            modifiedTime( Instant.now( clock ) ).
            email( "user1@enonic.com" ).
            login( "user1" ).
            build();

        final LocalScope localScope = ContextAccessor.current().getLocalScope();

        final AuthenticationInfo authInfo = AuthenticationInfo.create().user( user ).principals( RoleKeys.ADMIN_LOGIN ).build();
        localScope.setAttribute( authInfo );
        localScope.setSession( new SessionMock() );

        String jsonString = request().path( "auth/authenticated" ).get().getAsString();

        assertJson( "authenticated_success.json", jsonString );
    }

}
