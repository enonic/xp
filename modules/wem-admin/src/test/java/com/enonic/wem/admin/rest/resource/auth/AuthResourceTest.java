package com.enonic.wem.admin.rest.resource.auth;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.SecurityService;
import com.enonic.wem.api.security.User;
import com.enonic.wem.api.security.UserStoreKey;
import com.enonic.wem.api.security.auth.AuthenticationInfo;
import com.enonic.wem.api.security.auth.AuthenticationToken;

public class AuthResourceTest
    extends AbstractResourceTest
{

    private static final Instant NOW = Instant.ofEpochSecond( 0 );

    private static Clock clock = Clock.fixed( NOW, ZoneId.of( "UTC" ) );

    private SecurityService securityService;

    @Override
    protected Object getResourceInstance()
    {
        securityService = Mockito.mock( SecurityService.class );

        final AuthResource resource = new AuthResource();

        securityService = Mockito.mock( SecurityService.class );
        resource.setSecurityService( securityService );

        return resource;
    }

    @Test
    public void testLoginWithUsernameSuccess()
        throws Exception
    {
        final User user = User.create().
            key( PrincipalKey.ofUser( UserStoreKey.system(), "user1" ) ).
            displayName( "User 1" ).
            modifiedTime( Instant.now( clock ) ).
            email( "user1@enonic.com" ).
            login( "user1" ).
            build();
        final AuthenticationInfo authInfo = AuthenticationInfo.create().user( user ).build();
        Mockito.when( securityService.authenticate( Mockito.any( AuthenticationToken.class ) ) ).
            thenReturn( authInfo );

        String jsonString = request().path( "auth/login" ).
            entity( "{\"user\":\"user1\",\"password\":\"password\",\"userStore\":\"system\",\"rememberMe\":false}",
                    MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "login.json", jsonString );
    }

    @Test
    public void testLoginWithEmailSuccess()
        throws Exception
    {
        final User user = User.create().
            key( PrincipalKey.ofUser( UserStoreKey.system(), "user1" ) ).
            displayName( "User 1" ).
            modifiedTime( Instant.now( clock ) ).
            email( "user1@enonic.com" ).
            login( "user1" ).
            build();
        final AuthenticationInfo authInfo = AuthenticationInfo.create().user( user ).build();
        Mockito.when( securityService.authenticate( Mockito.any( AuthenticationToken.class ) ) ).
            thenReturn( authInfo );

        String jsonString = request().path( "auth/login" ).
            entity( "{\"user\":\"user1@enonic.com\",\"password\":\"password\",\"userStore\":\"system\",\"rememberMe\":false}",
                    MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "login.json", jsonString );
    }

    @Test
    public void testLoginFail()
        throws Exception
    {
        final AuthenticationInfo authInfo = AuthenticationInfo.failed();
        Mockito.when( securityService.authenticate( Mockito.any( AuthenticationToken.class ) ) ).
            thenReturn( authInfo );

        String jsonString = request().path( "auth/login" ).
            entity( "{\"user\":\"user1\",\"password\":\"password\",\"userStore\":\"system\",\"rememberMe\":false}",
                    MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "login_failed.json", jsonString );
    }

}