package com.enonic.xp.admin.impl.rest.resource.security;

import java.net.URLEncoder;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import com.google.common.collect.Iterables;

import com.enonic.xp.admin.impl.rest.resource.AdminResourceTestSupport;
import com.enonic.xp.admin.impl.rest.resource.security.json.CreateUserJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.UpdatePasswordJson;
import com.enonic.xp.security.CreateGroupParams;
import com.enonic.xp.security.CreateRoleParams;
import com.enonic.xp.security.CreateUserParams;
import com.enonic.xp.security.CreateUserStoreParams;
import com.enonic.xp.security.Group;
import com.enonic.xp.security.Principal;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.PrincipalNotFoundException;
import com.enonic.xp.security.PrincipalQuery;
import com.enonic.xp.security.PrincipalQueryResult;
import com.enonic.xp.security.PrincipalRelationship;
import com.enonic.xp.security.PrincipalRelationships;
import com.enonic.xp.security.PrincipalType;
import com.enonic.xp.security.Principals;
import com.enonic.xp.security.Role;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.UpdateGroupParams;
import com.enonic.xp.security.UpdateRoleParams;
import com.enonic.xp.security.UpdateUserParams;
import com.enonic.xp.security.UpdateUserStoreParams;
import com.enonic.xp.security.User;
import com.enonic.xp.security.UserStore;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.UserStores;
import com.enonic.xp.security.acl.UserStoreAccess;
import com.enonic.xp.security.acl.UserStoreAccessControlEntry;
import com.enonic.xp.security.acl.UserStoreAccessControlList;

import static com.enonic.xp.security.PrincipalRelationship.from;
import static com.enonic.xp.security.acl.UserStoreAccess.ADMINISTRATOR;
import static com.enonic.xp.security.acl.UserStoreAccess.READ;

public class SecurityResourceTest
    extends AdminResourceTestSupport
{
    private static final Instant NOW = Instant.ofEpochSecond( 0 );

    private static Clock clock = Clock.fixed( NOW, ZoneId.of( "UTC" ) );

    private SecurityService securityService;

    private static final UserStoreKey USER_STORE_1 = UserStoreKey.from( "local" );

    private static final UserStoreKey USER_STORE_2 = UserStoreKey.from( "file-store" );

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
    public void getUserStores()
        throws Exception
    {
        final UserStores userStores = createUserStores();

        Mockito.when( securityService.getUserStores() ).
            thenReturn( userStores );

        String jsonString = request().path( "security/userstore/list" ).get().getAsString();

        assertJson( "getUserstores.json", jsonString );
    }

    @Test
    public void getUserStoreByKey()
        throws Exception
    {
        final UserStore userStore = createUserStores().getUserStore( USER_STORE_1 );

        Mockito.when( securityService.getUserStore( USER_STORE_1 ) ).thenReturn( userStore );

        final User user1 = User.create().
            key( PrincipalKey.from( "user:local:user1" ) ).
            displayName( "User 1" ).
            login( "user1" ).
            modifiedTime( Instant.now( clock ) ).
            build();
        final Group group1 = Group.create().
            key( PrincipalKey.from( "group:local:mygroup" ) ).
            displayName( "My Group" ).
            description( "my group" ).
            modifiedTime( Instant.now( clock ) ).
            build();

        final Principals principals = Principals.from( user1, group1 );
        Mockito.when( securityService.getPrincipals( Mockito.isA( PrincipalKeys.class ) ) ).thenReturn( principals );

        final UserStoreAccessControlList userStorePermissions = UserStoreAccessControlList.create().
            add( UserStoreAccessControlEntry.create().principal( PrincipalKey.from( "user:local:user1" ) ).access(
                UserStoreAccess.CREATE_USERS ).build() ).
            add( UserStoreAccessControlEntry.create().principal( PrincipalKey.from( "group:local:mygroup" ) ).access(
                UserStoreAccess.USER_STORE_MANAGER ).build() ).
            build();
        Mockito.when( securityService.getUserStorePermissions( USER_STORE_1 ) ).thenReturn( userStorePermissions );

        String jsonString = request().path( "security/userstore" ).queryParam( "key", "local" ).get().getAsString();

        assertJson( "getUserstoreByKey.json", jsonString );
    }

    @Test
    public void getDefaultUserStorePermissions()
        throws Exception
    {
        final UserStoreAccessControlList userStoreAccessControlList = UserStoreAccessControlList.of(
            UserStoreAccessControlEntry.create().principal( PrincipalKey.from( "role:system.authenticated" ) ).access( READ ).build(),
            UserStoreAccessControlEntry.create().principal( PrincipalKey.from( "role:system.admin" ) ).access( ADMINISTRATOR ).build() );

        final Principals principals = Principals.from(
            Role.create().displayName( "Authenticated" ).key( PrincipalKey.from( "role:system.authenticated" ) ).description(
                "authenticated" ).build(),
            Role.create().displayName( "Administrator" ).key( PrincipalKey.from( "role:system.admin" ) ).description( "admin" ).build() );

        Mockito.when( securityService.getDefaultUserStorePermissions() ).thenReturn( userStoreAccessControlList );
        Mockito.when( securityService.getPrincipals( Mockito.isA( PrincipalKeys.class ) ) ).thenReturn( principals );

        String jsonString = request().path( "security/userstore/default" ).get().getAsString();

        assertJson( "getDefaultUserStorePermissions.json", jsonString );
    }

    @Test
    public void createUserStore()
        throws Exception
    {
        final User user1 = User.create().
            key( PrincipalKey.from( "user:system:user1" ) ).
            displayName( "User 1" ).
            login( "user1" ).
            modifiedTime( Instant.now( clock ) ).
            build();
        final Principals principals = Principals.from( user1 );
        Mockito.when( securityService.getPrincipals( Mockito.isA( PrincipalKeys.class ) ) ).thenReturn( principals );

        final UserStoreKey userStoreKey = UserStoreKey.from( "enonic" );
        final UserStoreAccessControlList permissions = UserStoreAccessControlList.of(
            UserStoreAccessControlEntry.create().principal( PrincipalKey.from( "user:system:user1" ) ).access( ADMINISTRATOR ).build() );
        final UserStore userStore = UserStore.create().
            key( UserStoreKey.from( "enonic" ) ).
            displayName( "Enonic User Store" ).
            build();
        Mockito.when( securityService.createUserStore( Mockito.isA( CreateUserStoreParams.class ) ) ).thenReturn( userStore );

        Mockito.when( securityService.getUserStorePermissions( userStoreKey ) ).thenReturn( permissions );

        String jsonString = request().path( "security/userstore/create" ).
            entity( readFromFile( "createUserStoreParams.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "createUserStoreSuccess.json", jsonString );
    }

    @Test
    public void updateUserStore()
        throws Exception
    {
        final User user1 = User.create().
            key( PrincipalKey.from( "user:system:user1" ) ).
            displayName( "User 1" ).
            login( "user1" ).
            modifiedTime( Instant.now( clock ) ).
            build();
        final Principals principals = Principals.from( user1 );
        Mockito.when( securityService.getPrincipals( Mockito.isA( PrincipalKeys.class ) ) ).thenReturn( principals );

        final UserStoreKey userStoreKey = UserStoreKey.from( "enonic" );
        final UserStoreAccessControlList permissions = UserStoreAccessControlList.of(
            UserStoreAccessControlEntry.create().principal( PrincipalKey.from( "user:system:user1" ) ).access( ADMINISTRATOR ).build() );
        final UserStore userStore = UserStore.create().
            key( UserStoreKey.from( "enonic" ) ).
            displayName( "Enonic User Store" ).
            build();
        Mockito.when( securityService.updateUserStore( Mockito.isA( UpdateUserStoreParams.class ) ) ).thenReturn( userStore );

        Mockito.when( securityService.getUserStorePermissions( userStoreKey ) ).thenReturn( permissions );

        String jsonString = request().path( "security/userstore/update" ).
            entity( readFromFile( "updateUserStoreParams.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "updateUserStoreSuccess.json", jsonString );
    }

    @Test
    public void getUsersFromPrincipals()
        throws Exception
    {
        final Principals principals = createPrincipalsFromUsers();
        assertArrayEquals( principals.getList().toArray(), Iterables.toArray( principals.getUsers(), User.class ) );
    }

    @Test
    public void getRolesFromPrincipals()
        throws Exception
    {
        final Principals principals = createPrincipalsFromRoles();
        assertArrayEquals( principals.getList().toArray(), Iterables.toArray( principals.getRoles(), Role.class ) );
    }

    @Test
    public void getGroupsFromPrincipals()
        throws Exception
    {
        final Principals principals = createPrincipalsFromGroups();
        assertArrayEquals( principals.getList().toArray(), Iterables.toArray( principals.getGroups(), Group.class ) );
    }

    @Test
    public void getPrincipals()
        throws Exception
    {
        final UserStores userStores = createUserStores();
        final Principals principals = createPrincipalsFromUsers();
        final List<PrincipalType> userTypes = new ArrayList<>();
        userTypes.add( PrincipalType.USER );
        Mockito.when( securityService.findPrincipals( userStores.get( 0 ).getKey(), userTypes, null ) ).
            thenReturn( principals );

        String jsonString = request().
            path( "security/principals" ).
            queryParam( "types", "user" ).
            queryParam( "userStoreKey", "local" ).
            get().getAsString();

        assertJson( "getPrincipals.json", jsonString );
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
        Mockito.<Optional<? extends Principal>>when( securityService.getPrincipal( PrincipalKey.from( "user:local:alice" ) ) ).thenReturn(
            userRes );

        String jsonString = request().
            path( "security/principals/" + URLEncoder.encode( "user:local:alice", "UTF-8" ) ).
            get().getAsString();

        assertJson( "getPrincipalUserById.json", jsonString );
    }

    @Test
    public void getPrincipalUserByIdWithMemberships()
        throws Exception
    {
        final User user1 = User.create().
            key( PrincipalKey.ofUser( USER_STORE_1, "a" ) ).
            displayName( "Alice" ).
            modifiedTime( Instant.now( clock ) ).
            email( "alice@a.org" ).
            login( "alice" ).
            build();
        final Group group1 = Group.create().
            key( PrincipalKey.ofGroup( UserStoreKey.system(), "group-a" ) ).
            displayName( "Group A" ).
            modifiedTime( Instant.now( clock ) ).
            description( "group a" ).
            build();
        final Group group2 = Group.create().
            key( PrincipalKey.ofGroup( UserStoreKey.system(), "group-b" ) ).
            displayName( "Group B" ).
            description( "group b" ).
            modifiedTime( Instant.now( clock ) ).
            build();

        final Optional<? extends Principal> userRes = Optional.of( user1 );
        Mockito.<Optional<? extends Principal>>when( securityService.getPrincipal( PrincipalKey.from( "user:local:a" ) ) ).thenReturn(
            userRes );
        final PrincipalKeys membershipKeys = PrincipalKeys.from( group1.getKey(), group2.getKey() );
        Mockito.when( securityService.getMemberships( PrincipalKey.from( "user:local:a" ) ) ).thenReturn( membershipKeys );
        final Principals memberships = Principals.from( group1, group2 );
        Mockito.when( securityService.getPrincipals( membershipKeys ) ).thenReturn( memberships );

        String jsonString = request().
            path( "security/principals/" + URLEncoder.encode( "user:local:a", "UTF-8" ) ).
            queryParam( "memberships", "true" ).
            get().getAsString();

        assertJson( "getPrincipalUserByIdWithMemberships.json", jsonString );
    }

    @Test
    public void getPrincipalGroupById()
        throws Exception
    {
        final Group group = Group.create().
            key( PrincipalKey.ofGroup( UserStoreKey.system(), "group-a" ) ).
            displayName( "Group A" ).
            description( "group a" ).
            modifiedTime( Instant.now( clock ) ).
            build();

        final Optional<? extends Principal> userRes = Optional.of( group );
        Mockito.<Optional<? extends Principal>>when(
            securityService.getPrincipal( PrincipalKey.from( "group:system:group-a" ) ) ).thenReturn( userRes );

        PrincipalRelationship membership1 = from( group.getKey() ).to( PrincipalKey.from( "user:system:user1" ) );
        PrincipalRelationship membership2 = from( group.getKey() ).to( PrincipalKey.from( "user:system:user2" ) );
        PrincipalRelationships memberships = PrincipalRelationships.from( membership1, membership2 );
        Mockito.when( securityService.getRelationships( PrincipalKey.from( "group:system:group-a" ) ) ).thenReturn( memberships );

        String jsonString = request().
            path( "security/principals/" + URLEncoder.encode( "group:system:group-a", "UTF-8" ) ).
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
            description( "super u" ).
            build();

        final Optional<? extends Principal> userRes = Optional.of( role );
        Mockito.<Optional<? extends Principal>>when( securityService.getPrincipal( PrincipalKey.from( "role:superuser" ) ) ).thenReturn(
            userRes );

        PrincipalRelationship membership1 = from( role.getKey() ).to( PrincipalKey.from( "user:system:user1" ) );
        PrincipalRelationship membership2 = from( role.getKey() ).to( PrincipalKey.from( "user:system:user2" ) );
        PrincipalRelationships memberships = PrincipalRelationships.from( membership1, membership2 );
        Mockito.when( securityService.getRelationships( PrincipalKey.from( "role:superuser" ) ) ).thenReturn( memberships );

        String jsonString = request().
            path( "security/principals/" + URLEncoder.encode( "role:superuser", "UTF-8" ) ).
            get().getAsString();

        assertJson( "getPrincipalRoleById.json", jsonString );
    }


    @Test
    public void isEmailAvailableNegative()
        throws Exception
    {
        final User user = User.create().
            key( PrincipalKey.ofUser( USER_STORE_1, "a" ) ).
            displayName( "Alice" ).
            modifiedTime( Instant.now( clock ) ).
            email( "alice@enonic.com" ).
            login( "alice" ).
            build();

        final PrincipalQueryResult queryResult = PrincipalQueryResult.create().addPrincipal( user ).totalSize( 1 ).build();
        Mockito.when( securityService.query( Mockito.any( PrincipalQuery.class ) ) ).thenReturn( queryResult );

        String jsonString = request().
            path( "security/principals/emailAvailable" ).
            queryParam( "email", "true" ).
            queryParam( "userStoreKey", "true" ).
            get().getAsString();

        assertJson( "emailNotAvailableSuccess.json", jsonString );
    }

    @Test
    public void isEmailAvailablePositive()
        throws Exception
    {
        final PrincipalQueryResult queryResult = PrincipalQueryResult.create().totalSize( 0 ).build();
        Mockito.when( securityService.query( Mockito.any( PrincipalQuery.class ) ) ).thenReturn( queryResult );

        String jsonString = request().
            path( "security/principals/emailAvailable" ).
            queryParam( "email", "true" ).
            queryParam( "userStoreKey", "true" ).
            get().getAsString();

        assertJson( "emailAvailableSuccess.json", jsonString );
    }

    @Test
    public void createUser()
        throws Exception
    {
        final User user = User.create().
            key( PrincipalKey.ofUser( USER_STORE_1, "user1" ) ).
            displayName( "User 1" ).
            modifiedTime( Instant.now( clock ) ).
            email( "user1@enonic.com" ).
            login( "user1" ).
            build();

        Mockito.when( securityService.createUser( Mockito.any( CreateUserParams.class ) ) ).thenReturn( user );

        String jsonString = request().
            path( "security/principals/createUser" ).
            entity( readFromFile( "createUserParams.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "createUserSuccess.json", jsonString );
    }

    @Test
    public void createGroup()
        throws Exception
    {
        final Group group = Group.create().
            key( PrincipalKey.ofGroup( UserStoreKey.system(), "group-a" ) ).
            displayName( "Group A" ).
            description( "group a" ).
            modifiedTime( Instant.now( clock ) ).
            build();

        Mockito.when( securityService.createGroup( Mockito.any( CreateGroupParams.class ) ) ).thenReturn( group );

        String jsonString = request().
            path( "security/principals/createGroup" ).
            entity( readFromFile( "createGroupParams.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "createGroupSuccess.json", jsonString );
    }

    @Test
    public void createRole()
        throws Exception
    {
        final Role role = Role.create().
            key( PrincipalKey.ofRole( "superuser" ) ).
            displayName( "Super user role" ).
            modifiedTime( Instant.now( clock ) ).
            description( "role" ).
            build();

        Mockito.when( securityService.createRole( Mockito.any( CreateRoleParams.class ) ) ).thenReturn( role );

        String jsonString = request().
            path( "security/principals/createRole" ).
            entity( readFromFile( "createRoleParams.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "createRoleSuccess.json", jsonString );
    }

    @Test
    public void updateUser()
        throws Exception
    {
        final User user = User.create().
            key( PrincipalKey.ofUser( USER_STORE_1, "user1" ) ).
            displayName( "User 1" ).
            modifiedTime( Instant.now( clock ) ).
            email( "user1@enonic.com" ).
            login( "user1" ).
            build();

        Mockito.when( securityService.updateUser( Mockito.any( UpdateUserParams.class ) ) ).thenReturn( user );

        String jsonString = request().
            path( "security/principals/updateUser" ).
            entity( readFromFile( "updateUserParams.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "createUserSuccess.json", jsonString );
    }

    @Test
    public void updateGroup()
        throws Exception
    {
        final Group group = Group.create().
            key( PrincipalKey.ofGroup( UserStoreKey.system(), "group-a" ) ).
            displayName( "Group A" ).
            description( "group a" ).
            modifiedTime( Instant.now( clock ) ).
            build();

        Mockito.when( securityService.updateGroup( Mockito.any( UpdateGroupParams.class ) ) ).thenReturn( group );
        PrincipalRelationship membership1 = from( group.getKey() ).to( PrincipalKey.from( "user:system:user1" ) );
        PrincipalRelationship membership2 = from( group.getKey() ).to( PrincipalKey.from( "user:system:user2" ) );
        PrincipalRelationships memberships = PrincipalRelationships.from( membership1, membership2 );
        Mockito.when( securityService.getRelationships( group.getKey() ) ).thenReturn( memberships );

        String jsonString = request().
            path( "security/principals/updateGroup" ).
            entity( readFromFile( "updateGroupParams.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "createGroupSuccess.json", jsonString );
    }

    @Test
    public void updateRole()
        throws Exception
    {
        final Role role = Role.create().
            key( PrincipalKey.ofRole( "superuser" ) ).
            displayName( "Super user role" ).
            modifiedTime( Instant.now( clock ) ).
            description( "role" ).
            build();

        Mockito.when( securityService.updateRole( Mockito.any( UpdateRoleParams.class ) ) ).thenReturn( role );
        PrincipalRelationship membership1 = from( role.getKey() ).to( PrincipalKey.from( "user:system:user1" ) );
        PrincipalRelationship membership2 = from( role.getKey() ).to( PrincipalKey.from( "user:system:user2" ) );
        PrincipalRelationships memberships = PrincipalRelationships.from( membership1, membership2 );
        Mockito.when( securityService.getRelationships( role.getKey() ) ).thenReturn( memberships );

        String jsonString = request().
            path( "security/principals/updateRole" ).
            entity( readFromFile( "updateRoleParams.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "createRoleSuccess.json", jsonString );
    }

    @Test
    public void deletePrincipals()
        throws Exception
    {
        final PrincipalKey user1 = PrincipalKey.from( "user:system:user1" );
        Mockito.doThrow( new PrincipalNotFoundException( user1 ) ).when( securityService ).deletePrincipal( user1 );

        String jsonString = request().
            path( "security/principals/delete" ).
            entity( readFromFile( "deletePrincipalsParams.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "deletePrincipalsResult.json", jsonString );
    }

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void setPassword_null_throws_exception()
        throws Exception
    {
        final SecurityResource resource = (SecurityResource) getResourceInstance();
        final UpdatePasswordJson params = new UpdatePasswordJson( "user:system:user1", null );

        exception.expect( WebApplicationException.class );
        resource.setPassword( params );
    }

    @Test
    public void setPassword_empty_throws_exception()
        throws Exception
    {
        final SecurityResource resource = (SecurityResource) getResourceInstance();
        final UpdatePasswordJson params = new UpdatePasswordJson( "user:system:user1", "" );

        exception.expect( WebApplicationException.class );
        resource.setPassword( params );
    }

    @Test
    public void createUser_password_null_throws_exception()
        throws Exception
    {
        final SecurityResource resource = (SecurityResource) getResourceInstance();
        final CreateUserJson params = new CreateUserJson();
        params.userKey = "user:system:user1";
        params.displayName = "usert1";
        params.email = "test@enonic.com";
        params.login = "test";
        params.password = null;

        exception.expect( WebApplicationException.class );
        resource.createUser( params );
    }

    @Test
    public void createUser_password_empty_throws_exception()
        throws Exception
    {
        final SecurityResource resource = (SecurityResource) getResourceInstance();
        final CreateUserJson params = new CreateUserJson();
        params.userKey = "user:system:user1";
        params.displayName = "usert1";
        params.email = "test@enonic.com";
        params.login = "test";
        params.password = "";

        exception.expect( WebApplicationException.class );
        resource.createUser( params );
    }

    private UserStores createUserStores()
    {
        final UserStore userStore1 = UserStore.create().
            key( USER_STORE_1 ).
            displayName( "Local LDAP" ).
            build();

        final UserStore userStore2 = UserStore.create().
            key( USER_STORE_2 ).
            displayName( "File based user store" ).
            build();

        return UserStores.from( userStore1, userStore2 );
    }

    private Principals createPrincipalsFromUsers()
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

    private Principals createPrincipalsFromRoles()
    {
        final Role role1 = Role.create().
            key( PrincipalKey.ofRole( "a" ) ).
            displayName( "Destructors" ).
            modifiedTime( Instant.now( clock ) ).
            build();

        final Role role2 = Role.create().
            key( PrincipalKey.ofRole( "b" ) ).
            displayName( "Overlords" ).
            modifiedTime( Instant.now( clock ) ).
            build();
        return Principals.from( role1, role2 );
    }

    private Principals createPrincipalsFromGroups()
    {
        final Group group1 = Group.create().
            key( PrincipalKey.ofGroup( USER_STORE_1, "a" ) ).
            displayName( "Destructors" ).
            modifiedTime( Instant.now( clock ) ).
            build();

        final Group group2 = Group.create().
            key( PrincipalKey.ofGroup( USER_STORE_2, "b" ) ).
            displayName( "Overlords" ).
            modifiedTime( Instant.now( clock ) ).
            build();
        return Principals.from( group1, group2 );
    }
}
