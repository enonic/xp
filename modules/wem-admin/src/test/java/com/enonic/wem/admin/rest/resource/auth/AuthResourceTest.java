package com.enonic.wem.admin.rest.resource.auth;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest;
import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.context.LocalScope;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.RoleKeys;
import com.enonic.wem.api.security.SecurityService;
import com.enonic.wem.api.security.User;
import com.enonic.wem.api.security.UserStore;
import com.enonic.wem.api.security.UserStoreKey;
import com.enonic.wem.api.security.UserStores;
import com.enonic.wem.api.security.auth.AuthenticationInfo;
import com.enonic.wem.api.security.auth.AuthenticationToken;
import com.enonic.wem.api.session.SessionKey;
import com.enonic.wem.api.session.SimpleSession;

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
        final UserStore us1 = UserStore.newUserStore().key( new UserStoreKey( "remote" ) ).displayName( "Remote" ).build();
        final UserStore us2 = UserStore.newUserStore().key( UserStoreKey.system() ).displayName( "System" ).build();
        final UserStores userStores = UserStores.from( us1, us2 );
        Mockito.when( securityService.getUserStores() ).thenReturn( userStores );

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
        final AuthenticationInfo authInfo = AuthenticationInfo.create().user( user ).principals( RoleKeys.ADMIN_LOGIN ).build();
        Mockito.when( securityService.authenticate( Mockito.any( AuthenticationToken.class ) ) ).
            thenReturn( authInfo );

        String jsonString = request().path( "auth/login" ).
            entity( "{\"user\":\"user1\",\"password\":\"password\",\"rememberMe\":false}", MediaType.APPLICATION_JSON_TYPE ).
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
        final AuthenticationInfo authInfo = AuthenticationInfo.create().user( user ).principals( RoleKeys.ADMIN_LOGIN ).build();
        Mockito.when( securityService.authenticate( Mockito.any( AuthenticationToken.class ) ) ).
            thenReturn( authInfo );

        String jsonString = request().path( "auth/login" ).
            entity( "{\"user\":\"user1@enonic.com\",\"password\":\"password\",\"rememberMe\":false}", MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "login.json", jsonString );
    }

    @Test
    public void testLoginWithUserNameUserStore()
        throws Exception
    {
        final User user = User.create().
            key( PrincipalKey.ofUser( UserStoreKey.system(), "user1" ) ).
            displayName( "User 1" ).
            modifiedTime( Instant.now( clock ) ).
            email( "user1@enonic.com" ).
            login( "user1" ).
            build();
        final AuthenticationInfo authInfo = AuthenticationInfo.create().user( user ).principals( RoleKeys.ADMIN_LOGIN ).build();
        Mockito.when( securityService.authenticate( Mockito.any( AuthenticationToken.class ) ) ).
            thenReturn( authInfo );

        String jsonString = request().path( "auth/login" ).
            entity( "{\"user\":\"system\\\\user1\",\"password\":\"password\",\"rememberMe\":false}", MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "login.json", jsonString );
    }

    @Test
    public void testLoginFail()
        throws Exception
    {
        final AuthenticationInfo authInfo = AuthenticationInfo.unAuthenticated();
        Mockito.when( securityService.authenticate( Mockito.any( AuthenticationToken.class ) ) ).
            thenReturn( authInfo );

        String jsonString = request().path( "auth/login" ).
            entity( "{\"user\":\"user1\",\"password\":\"password\",\"rememberMe\":false}", MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "login_failed.json", jsonString );
    }

    @Test
    public void testLoginUserWithoutLoginAdminRoleFail()
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
            entity( "{\"user\":\"user1\",\"password\":\"password\",\"rememberMe\":false}", MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "login_failed.json", jsonString );
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
            key( PrincipalKey.ofUser( UserStoreKey.system(), "user1" ) ).
            displayName( "User 1" ).
            modifiedTime( Instant.now( clock ) ).
            email( "user1@enonic.com" ).
            login( "user1" ).
            build();

        final LocalScope localScope = ContextAccessor.current().getLocalScope();

        final AuthenticationInfo authInfo = AuthenticationInfo.create().user( user ).principals( RoleKeys.ADMIN_LOGIN ).build();
        localScope.setAttribute( authInfo );
        localScope.setSession( new SimpleSession( SessionKey.generate() ) );

        String jsonString = request().path( "auth/authenticated" ).get().getAsString();

        assertJson( "authenticated_success.json", jsonString );
    }

}