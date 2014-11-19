package com.enonic.wem.admin.rest.resource.security;

import java.net.URLEncoder;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest;
import com.enonic.wem.api.security.Group;
import com.enonic.wem.api.security.Principal;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.PrincipalRelationship;
import com.enonic.wem.api.security.PrincipalRelationships;
import com.enonic.wem.api.security.PrincipalType;
import com.enonic.wem.api.security.Principals;
import com.enonic.wem.api.security.Role;
import com.enonic.wem.api.security.SecurityService;
import com.enonic.wem.api.security.User;
import com.enonic.wem.api.security.UserStore;
import com.enonic.wem.api.security.UserStoreKey;
import com.enonic.wem.api.security.UserStores;

import static com.enonic.wem.api.security.PrincipalRelationship.from;

public class SecurityResourceTest
    extends AbstractResourceTest
{
    private static final Instant NOW = Instant.ofEpochSecond( 0 );

    private static Clock clock = Clock.fixed( NOW, ZoneId.of( "UTC" ) );

    private SecurityService securityService;

    private static final UserStoreKey USER_STORE_1 = new UserStoreKey( "local" );

    private static final UserStoreKey USER_STORE_2 = new UserStoreKey( "file-store" );

    @Override
    protected Object getResourceInstance()
    {
        securityService = Mockito.mock( SecurityService.class );

        final SecurityResource resource = new SecurityResource();

        securityService = Mockito.mock( SecurityService.class );
        resource.setSecurityService( securityService );

        return resource;
    }

    @Test
    public void get_userStores()
        throws Exception
    {
        final UserStores userStores = createUserStores();

        Mockito.when( securityService.getUserStores() ).
            thenReturn( userStores );

        String jsonString = request().path( "security/userstore/list" ).get().getAsString();

        assertJson( "get_userstores.json", jsonString );
    }

    @Test
    public void get_principals()
        throws Exception
    {
        final UserStores userStores = createUserStores();
        final Principals principals = createPrincipals();
        Mockito.when( securityService.getPrincipals( userStores.get( 0 ).getKey(), PrincipalType.USER ) ).
            thenReturn( principals );

        String jsonString = request().
            path( "security/principals" ).
            queryParam( "type", "user" ).
            queryParam( "userStoreKey", "local" ).
            get().getAsString();

        assertJson( "get_principals.json", jsonString );
    }

    @Test
    public void getPrincipalUserById()
        throws Exception
    {
        final User user1 = User.create().
            key( PrincipalKey.ofUser( USER_STORE_1, "a" ) ).
            displayName( "Alice" ).
            modifiedTime( Instant.now( clock ) ).
            email( "alice@a.org" ).
            login( "alice" ).
            build();

        final Optional<? extends Principal> userRes = Optional.of( user1 );
        Mockito.<Optional<? extends Principal>>when( securityService.getPrincipal( PrincipalKey.from( "local:user:alice" ) ) ).thenReturn(
            userRes );

        String jsonString = request().
            path( "security/principals/" + URLEncoder.encode( "local:user:alice", "UTF-8" ) ).
            get().getAsString();

        assertJson( "getPrincipalUserById.json", jsonString );
    }

    @Test
    public void getPrincipalGroupById()
        throws Exception
    {
        final Group group = Group.create().
            key( PrincipalKey.ofGroup( UserStoreKey.system(), "group-a" ) ).
            displayName( "Group A" ).
            modifiedTime( Instant.now( clock ) ).
            build();

        final Optional<? extends Principal> userRes = Optional.of( group );
        Mockito.<Optional<? extends Principal>>when(
            securityService.getPrincipal( PrincipalKey.from( "system:group:group-a" ) ) ).thenReturn( userRes );

        PrincipalRelationship membership1 = from( group.getKey() ).to( PrincipalKey.from( "system:user:user1" ) );
        PrincipalRelationship membership2 = from( group.getKey() ).to( PrincipalKey.from( "system:user:user2" ) );
        PrincipalRelationships memberships = PrincipalRelationships.from( membership1, membership2 );
        Mockito.when( securityService.getRelationships( PrincipalKey.from( "system:group:group-a" ) ) ).thenReturn( memberships );

        String jsonString = request().
            path( "security/principals/" + URLEncoder.encode( "system:group:group-a", "UTF-8" ) ).
            get().getAsString();

        assertJson( "getPrincipalGroupById.json", jsonString );
    }

    @Test
    public void getPrincipalRoleById()
        throws Exception
    {
        final Role role = Role.create().
            key( PrincipalKey.ofRole( "superuser" ) ).
            displayName( "Super user role" ).
            modifiedTime( Instant.now( clock ) ).
            build();

        final Optional<? extends Principal> userRes = Optional.of( role );
        Mockito.<Optional<? extends Principal>>when(
            securityService.getPrincipal( PrincipalKey.from( "system:role:superuser" ) ) ).thenReturn( userRes );

        PrincipalRelationship membership1 = from( role.getKey() ).to( PrincipalKey.from( "system:user:user1" ) );
        PrincipalRelationship membership2 = from( role.getKey() ).to( PrincipalKey.from( "system:user:user2" ) );
        PrincipalRelationships memberships = PrincipalRelationships.from( membership1, membership2 );
        Mockito.when( securityService.getRelationships( PrincipalKey.from( "system:role:superuser" ) ) ).thenReturn( memberships );

        String jsonString = request().
            path( "security/principals/" + URLEncoder.encode( "system:role:superuser", "UTF-8" ) ).
            get().getAsString();

        assertJson( "getPrincipalRoleById.json", jsonString );
    }

    private UserStores createUserStores()
    {
        final UserStore userStore1 = UserStore.newUserStore().
            key( USER_STORE_1 ).
            displayName( "Local LDAP" ).
            build();

        final UserStore userStore2 = UserStore.newUserStore().
            key( USER_STORE_2 ).
            displayName( "File based user store" ).
            build();

        return UserStores.from( userStore1, userStore2 );
    }

    private Principals createPrincipals()
    {
        final User user1 = User.create().
            key( PrincipalKey.ofUser( USER_STORE_1, "a" ) ).
            displayName( "Alice" ).
            modifiedTime( Instant.now( clock ) ).
            email( "alice@a.org" ).
            login( "alice" ).
            build();

        final User user2 = User.create().
            key( PrincipalKey.ofUser( USER_STORE_2, "b" ) ).
            displayName( "Bobby" ).
            modifiedTime( Instant.now( clock ) ).
            email( "bobby@b.org" ).
            login( "bobby" ).
            build();
        return Principals.from( user1, user2 );
    }
}
