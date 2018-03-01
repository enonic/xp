package com.enonic.xp.core.impl.security;

import java.util.concurrent.Callable;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.internal.blobstore.MemoryBlobStore;
import com.enonic.xp.repo.impl.binary.BinaryServiceImpl;
import com.enonic.xp.repo.impl.branch.storage.BranchServiceImpl;
import com.enonic.xp.repo.impl.elasticsearch.AbstractElasticsearchIntegrationTest;
import com.enonic.xp.repo.impl.elasticsearch.IndexServiceInternalImpl;
import com.enonic.xp.repo.impl.elasticsearch.search.SearchDaoImpl;
import com.enonic.xp.repo.impl.elasticsearch.storage.StorageDaoImpl;
import com.enonic.xp.repo.impl.index.IndexServiceImpl;
import com.enonic.xp.repo.impl.node.NodeServiceImpl;
import com.enonic.xp.repo.impl.node.dao.NodeVersionServiceImpl;
import com.enonic.xp.repo.impl.repository.NodeRepositoryServiceImpl;
import com.enonic.xp.repo.impl.repository.RepositoryEntryServiceImpl;
import com.enonic.xp.repo.impl.repository.RepositoryServiceImpl;
import com.enonic.xp.repo.impl.search.NodeSearchServiceImpl;
import com.enonic.xp.repo.impl.storage.IndexDataServiceImpl;
import com.enonic.xp.repo.impl.storage.NodeStorageServiceImpl;
import com.enonic.xp.repo.impl.version.VersionServiceImpl;
import com.enonic.xp.security.CreateGroupParams;
import com.enonic.xp.security.CreateRoleParams;
import com.enonic.xp.security.CreateUserParams;
import com.enonic.xp.security.CreateUserStoreParams;
import com.enonic.xp.security.Group;
import com.enonic.xp.security.PrincipalAlreadyExistsException;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.PrincipalQuery;
import com.enonic.xp.security.PrincipalQueryResult;
import com.enonic.xp.security.PrincipalRelationship;
import com.enonic.xp.security.PrincipalRelationships;
import com.enonic.xp.security.Role;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityConstants;
import com.enonic.xp.security.UpdateGroupParams;
import com.enonic.xp.security.UpdateRoleParams;
import com.enonic.xp.security.UpdateUserParams;
import com.enonic.xp.security.UpdateUserStoreParams;
import com.enonic.xp.security.User;
import com.enonic.xp.security.UserStore;
import com.enonic.xp.security.UserStoreAlreadyExistsException;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.acl.UserStoreAccessControlEntry;
import com.enonic.xp.security.acl.UserStoreAccessControlList;
import com.enonic.xp.security.auth.AuthenticationException;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.security.auth.AuthenticationToken;
import com.enonic.xp.security.auth.EmailPasswordAuthToken;
import com.enonic.xp.security.auth.UsernamePasswordAuthToken;
import com.enonic.xp.security.auth.VerifiedEmailAuthToken;
import com.enonic.xp.security.auth.VerifiedUsernameAuthToken;

import static com.enonic.xp.security.acl.UserStoreAccess.ADMINISTRATOR;
import static com.enonic.xp.security.acl.UserStoreAccess.CREATE_USERS;
import static com.enonic.xp.security.acl.UserStoreAccess.WRITE_USERS;
import static org.junit.Assert.*;

public class SecurityServiceImplTest
    extends AbstractElasticsearchIntegrationTest
{
    private static final UserStoreKey SYSTEM = UserStoreKey.system();

    protected EventPublisher eventPublisher;

    private SecurityServiceImpl securityService;

    private NodeServiceImpl nodeService;

    private IndexServiceInternalImpl indexServiceInternal;

    private RepositoryServiceImpl repositoryService;

    @Override
    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();

        final MemoryBlobStore blobStore = new MemoryBlobStore();

        final BinaryServiceImpl binaryService = new BinaryServiceImpl();
        binaryService.setBlobStore( blobStore );

        final StorageDaoImpl storageDao = new StorageDaoImpl();
        storageDao.setClient( this.client );

        final SearchDaoImpl searchDao = new SearchDaoImpl();
        searchDao.setClient( this.client );

        final BranchServiceImpl branchService = new BranchServiceImpl();
        branchService.setStorageDao( storageDao );
        branchService.setSearchDao( searchDao );

        final VersionServiceImpl versionService = new VersionServiceImpl();
        versionService.setStorageDao( storageDao );

        final NodeVersionServiceImpl nodeDao = new NodeVersionServiceImpl();
        nodeDao.setBlobStore( blobStore );

        this.indexServiceInternal = new IndexServiceInternalImpl();
        this.indexServiceInternal.setClient( client );

        final NodeSearchServiceImpl searchService = new NodeSearchServiceImpl();
        searchService.setSearchDao( searchDao );

        IndexDataServiceImpl indexedDataService = new IndexDataServiceImpl();
        indexedDataService.setStorageDao( storageDao );

        final NodeStorageServiceImpl storageService = new NodeStorageServiceImpl();
        storageService.setBranchService( branchService );
        storageService.setVersionService( versionService );
        storageService.setNodeVersionService( nodeDao );
        storageService.setIndexDataService( indexedDataService );

        final NodeRepositoryServiceImpl nodeRepositoryService = new NodeRepositoryServiceImpl();
        nodeRepositoryService.setIndexServiceInternal( this.indexServiceInternal );

        final RepositoryEntryServiceImpl repositoryEntryService = new RepositoryEntryServiceImpl();
        repositoryEntryService.setIndexServiceInternal( this.indexServiceInternal );
        repositoryEntryService.setNodeRepositoryService( nodeRepositoryService );
        repositoryEntryService.setNodeStorageService( storageService );
        repositoryEntryService.setEventPublisher( Mockito.mock( EventPublisher.class ) );

        this.repositoryService = new RepositoryServiceImpl();
        this.repositoryService.setRepositoryEntryService( repositoryEntryService );
        this.repositoryService.setIndexServiceInternal( this.indexServiceInternal );
        this.repositoryService.setNodeRepositoryService( nodeRepositoryService );
        this.repositoryService.setNodeStorageService( storageService );
        this.repositoryService.setNodeSearchService( searchService );
        this.repositoryService.initialize();

        this.eventPublisher = Mockito.mock( EventPublisher.class );
        this.nodeService = new NodeServiceImpl();
        this.nodeService.setIndexServiceInternal( indexServiceInternal );
        this.nodeService.setNodeSearchService( searchService );
        this.nodeService.setNodeStorageService( storageService );
        this.nodeService.setBinaryService( binaryService );
        this.nodeService.setRepositoryService( repositoryService );
        this.nodeService.setEventPublisher( this.eventPublisher );
        this.nodeService.initialize();

        IndexServiceImpl indexService = new IndexServiceImpl();
        indexService.setNodeSearchService( searchService );
        indexService.setIndexServiceInternal( this.indexServiceInternal );

        securityService = new SecurityServiceImpl();
        securityService.setNodeService( this.nodeService );
        securityService.setIndexService( indexService );

        runAsAdmin( () -> securityService.initialize() );

    }

    @Test
    public void testCreateUser()
        throws Exception
    {
        runAsAdmin( () -> {
            final PrincipalKey userKey1 = PrincipalKey.ofUser( SYSTEM, "User1" );
            final CreateUserParams createUser1 = CreateUserParams.create().
                userKey( userKey1 ).
                displayName( "User 1" ).
                email( "user1@enonic.com" ).
                login( "User1" ).
                password( "123456" ).
                build();

            final PrincipalKey userKey2 = PrincipalKey.ofUser( SYSTEM, "user2" );
            final CreateUserParams createUser2 = CreateUserParams.create().
                userKey( userKey2 ).
                displayName( "User 2" ).
                email( "user2@enonic.com" ).
                login( "user2" ).
                build();

            final User user1 = securityService.createUser( createUser1 );
            final User user2 = securityService.createUser( createUser2 );
            refresh();

            final User createdUser1 = securityService.getUser( userKey1 ).get();
            final User createdUser2 = securityService.getUser( userKey2 ).get();

            assertEquals( "User 1", user1.getDisplayName() );
            assertEquals( "user1@enonic.com", user1.getEmail() );
            assertEquals( "User1", user1.getLogin() );
            assertEquals( "User 1", createdUser1.getDisplayName() );
            assertEquals( "user1@enonic.com", createdUser1.getEmail() );
            assertEquals( "User1", createdUser1.getLogin() );

            assertEquals( "User 2", user2.getDisplayName() );
            assertEquals( "user2@enonic.com", user2.getEmail() );
            assertEquals( "user2", user2.getLogin() );
            assertEquals( "User 2", createdUser2.getDisplayName() );
            assertEquals( "user2@enonic.com", createdUser2.getEmail() );
            assertEquals( "user2", createdUser2.getLogin() );
        } );
    }

    @Test(expected = PrincipalAlreadyExistsException.class)
    public void testCreateUserThrowsExceptionWhenNameIsOccupied()
        throws Exception
    {
        runAsAdmin( () -> {
            final PrincipalKey userKey1 = PrincipalKey.ofUser( SYSTEM, "User1" );
            final CreateUserParams createUser1 = CreateUserParams.create().
                userKey( userKey1 ).
                displayName( "User 1" ).
                email( "user1@enonic.com" ).
                login( "User1" ).
                password( "123456" ).
                build();

            securityService.createUser( createUser1 );
            securityService.createUser( createUser1 );
        } );
    }

    @Test
    public void testCreateUserDuplicatedEmail()
    {
        try
        {
            runAsAdmin( () -> {
                final PrincipalKey userKey1 = PrincipalKey.ofUser( SYSTEM, "User1" );
                final CreateUserParams createUser1 = CreateUserParams.create().
                    userKey( userKey1 ).
                    displayName( "User 1" ).
                    email( "same_email@enonic.com" ).
                    login( "User1" ).
                    password( "123456" ).
                    build();

                final PrincipalKey userKey2 = PrincipalKey.ofUser( SYSTEM, "user2" );
                final CreateUserParams createUser2 = CreateUserParams.create().
                    userKey( userKey2 ).
                    displayName( "User 2" ).
                    email( "same_email@enonic.com" ).
                    login( "user2" ).
                    build();

                securityService.createUser( createUser1 );
                securityService.createUser( createUser2 );
            } );

            fail( "Expected exception" );
        }
        catch ( IllegalArgumentException e )
        {
            assertEquals( "A user with email 'same_email@enonic.com' already exists in user store 'system'", e.getMessage() );
        }
    }

    @Test
    public void testUpdateUser()
        throws Exception
    {
        runAsAdmin( () -> {
            final CreateUserParams createUser = CreateUserParams.create().
                userKey( PrincipalKey.ofUser( SYSTEM, "User1" ) ).
                displayName( "User 1" ).
                email( "user1@enonic.com" ).
                login( "User1" ).
                build();

            final User user = securityService.createUser( createUser );
            refresh();

            final UpdateUserParams updateUserParams = UpdateUserParams.create( user ).
                email( "u2@enonic.net" ).
                build();
            final User updateUserResult = securityService.updateUser( updateUserParams );
            refresh();

            final User updatedUser = securityService.getUser( user.getKey() ).get();

            assertEquals( "u2@enonic.net", updateUserResult.getEmail() );
            assertEquals( "u2@enonic.net", updatedUser.getEmail() );

            assertEquals( "User1", updatedUser.getLogin() );
            assertEquals( "User 1", updatedUser.getDisplayName() );
            assertEquals( PrincipalKey.ofUser( SYSTEM, "User1" ), updatedUser.getKey() );
        } );
    }

    @Test
    public void testUpdateUserDuplicatedEmail()
    {
        try
        {
            runAsAdmin( () -> {
                final PrincipalKey userKey1 = PrincipalKey.ofUser( SYSTEM, "User1" );
                final CreateUserParams createUser1 = CreateUserParams.create().
                    userKey( userKey1 ).
                    displayName( "User 1" ).
                    email( "same_email@enonic.com" ).
                    login( "User1" ).
                    password( "123456" ).
                    build();

                final PrincipalKey userKey2 = PrincipalKey.ofUser( SYSTEM, "user2" );
                final CreateUserParams createUser2 = CreateUserParams.create().
                    userKey( userKey2 ).
                    displayName( "User 2" ).
                    email( "same_email@enonic.com" ).
                    login( "user2" ).
                    build();

                final User user1 = securityService.createUser( createUser1 );
                securityService.createUser( createUser2 );

                final UpdateUserParams updateUserParams = UpdateUserParams.create( user1 ).
                    email( "same_email@enonic.com" ).
                    build();
                securityService.updateUser( updateUserParams );
                refresh();
            } );

            fail( "Expected exception" );
        }
        catch ( IllegalArgumentException e )
        {
            assertEquals( "A user with email 'same_email@enonic.com' already exists in user store 'system'", e.getMessage() );
        }
    }

    @Test
    public void testUpdateUserDuplicatedEmailWithEditor()
    {
        try
        {
            runAsAdmin( () -> {
                final PrincipalKey userKey1 = PrincipalKey.ofUser( SYSTEM, "User1" );
                final CreateUserParams createUser1 = CreateUserParams.create().
                    userKey( userKey1 ).
                    displayName( "User 1" ).
                    email( "same_email@enonic.com" ).
                    login( "User1" ).
                    password( "123456" ).
                    build();

                final PrincipalKey userKey2 = PrincipalKey.ofUser( SYSTEM, "user2" );
                final CreateUserParams createUser2 = CreateUserParams.create().
                    userKey( userKey2 ).
                    displayName( "User 2" ).
                    email( "same_email@enonic.com" ).
                    login( "user2" ).
                    build();

                final User user1 = securityService.createUser( createUser1 );
                securityService.createUser( createUser2 );

                final UpdateUserParams updateUserParams = UpdateUserParams.create( user1 ).
                    editor( editableUser -> editableUser.email = "same_email@enonic.com" ).
                    build();
                securityService.updateUser( updateUserParams );
                refresh();
            } );

            fail( "Expected exception" );
        }
        catch ( IllegalArgumentException e )
        {
            assertEquals( "A user with email 'same_email@enonic.com' already exists in user store 'system'", e.getMessage() );
        }
    }

    @Test
    public void testCreateGroup()
        throws Exception
    {
        runAsAdmin( () -> {
            final PrincipalKey groupKey1 = PrincipalKey.ofGroup( SYSTEM, "Group-a" );
            final CreateGroupParams createGroup = CreateGroupParams.create().
                groupKey( groupKey1 ).
                displayName( "Group A" ).
                description( "Group A Description" ).
                build();

            final PrincipalKey groupKey2 = PrincipalKey.ofGroup( SYSTEM, "group-b" );
            final CreateGroupParams createGroup2 = CreateGroupParams.create().
                groupKey( groupKey2 ).
                displayName( "Group B" ).
                build();

            final Group group1 = securityService.createGroup( createGroup );
            final Group group2 = securityService.createGroup( createGroup2 );
            refresh();

            final Group createdGroup1 = securityService.getGroup( groupKey1 ).get();
            final Group createdGroup2 = securityService.getGroup( groupKey2 ).get();

            assertEquals( "Group A", group1.getDisplayName() );
            assertEquals( "Group A", createdGroup1.getDisplayName() );
            assertEquals( "Group A Description", group1.getDescription() );
            assertEquals( "Group A Description", createdGroup1.getDescription() );

            assertEquals( "Group B", group2.getDisplayName() );
            assertEquals( "Group B", createdGroup2.getDisplayName() );
            assertNull( group2.getDescription() );
            assertNull( createdGroup2.getDescription() );
        } );
    }

    @Test(expected = PrincipalAlreadyExistsException.class)
    public void testCreateGroupThrowsExceptionWhenNameIsOccupied()
        throws Exception
    {
        runAsAdmin( () -> {
            final PrincipalKey groupKey1 = PrincipalKey.ofGroup( SYSTEM, "Group-a" );
            final CreateGroupParams createGroup = CreateGroupParams.create().
                groupKey( groupKey1 ).
                displayName( "Group A" ).
                description( "Group A Description" ).
                build();

            securityService.createGroup( createGroup );
            securityService.createGroup( createGroup );
        } );
    }

    @Test
    public void testUpdateGroup()
        throws Exception
    {
        runAsAdmin( () -> {
            final CreateGroupParams createGroup = CreateGroupParams.create().
                groupKey( PrincipalKey.ofGroup( SYSTEM, "Group-a" ) ).
                displayName( "Group A" ).
                build();

            final Group group = securityService.createGroup( createGroup );
            refresh();

            final UpdateGroupParams groupUpdate = UpdateGroupParams.create( group ).
                displayName( "___Group B___" ).
                description( "description" ).
                build();
            final Group updatedGroupResult = securityService.updateGroup( groupUpdate );
            refresh();

            final Group updatedGroup = securityService.getGroup( group.getKey() ).get();
            assertEquals( "___Group B___", updatedGroupResult.getDisplayName() );
            assertEquals( "___Group B___", updatedGroup.getDisplayName() );
            assertEquals( "description", updatedGroupResult.getDescription() );
            assertEquals( "description", updatedGroup.getDescription() );
        } );
    }

    @Test
    public void testCreateRole()
        throws Exception
    {
        runAsAdmin( () -> {
            final PrincipalKey roleKey1 = PrincipalKey.ofRole( "Role-a" );
            final CreateRoleParams createRole = CreateRoleParams.create().
                roleKey( roleKey1 ).
                displayName( "Role A" ).
                description( "Group A Description" ).
                build();

            final PrincipalKey roleKey2 = PrincipalKey.ofRole( "role-b" );
            final CreateRoleParams createRole2 = CreateRoleParams.create().
                roleKey( roleKey2 ).
                displayName( "Role B" ).
                build();

            final Role role1 = securityService.createRole( createRole );
            final Role role2 = securityService.createRole( createRole2 );

            final Role createdRole1 = securityService.getRole( roleKey1 ).get();
            final Role createdRole2 = securityService.getRole( roleKey2 ).get();

            assertEquals( "Role A", role1.getDisplayName() );
            assertEquals( "Role A", createdRole1.getDisplayName() );
            assertEquals( "Group A Description", role1.getDescription() );
            assertEquals( "Group A Description", createdRole1.getDescription() );

            assertEquals( "Role B", role2.getDisplayName() );
            assertEquals( "Role B", createdRole2.getDisplayName() );
            assertNull( role2.getDescription() );
            assertNull( createdRole2.getDescription() );
        } );
    }

    @Test(expected = PrincipalAlreadyExistsException.class)
    public void testCreateRoleThrowsExceptionWhenNameIsOccupied()
        throws Exception
    {
        runAsAdmin( () -> {
            final PrincipalKey roleKey1 = PrincipalKey.ofRole( "Role-a" );
            final CreateRoleParams createRole = CreateRoleParams.create().
                roleKey( roleKey1 ).
                displayName( "Role A" ).
                description( "Group A Description" ).
                build();

            securityService.createRole( createRole );
            securityService.createRole( createRole );
        } );
    }

    @Test
    public void testUpdateRole()
        throws Exception
    {
        runAsAdmin( () -> {
            final CreateRoleParams createRole = CreateRoleParams.create().
                roleKey( PrincipalKey.ofRole( "Role-a" ) ).
                displayName( "Role A" ).
                build();

            final Role role = securityService.createRole( createRole );

            final UpdateRoleParams roleUpdate = UpdateRoleParams.create( role ).
                displayName( "___Role B___" ).
                description( "description" ).
                build();
            final Role updatedRoleResult = securityService.updateRole( roleUpdate );
            refresh();

            final Role updatedRole = securityService.getRole( role.getKey() ).get();
            assertEquals( "___Role B___", updatedRoleResult.getDisplayName() );
            assertEquals( "___Role B___", updatedRole.getDisplayName() );
            assertEquals( "description", updatedRoleResult.getDescription() );
            assertEquals( "description", updatedRole.getDescription() );
        } );
    }

    @Test
    public void testAddRelationship()
        throws Exception
    {
        runAsAdmin( () -> {
            // set up
            final PrincipalKey userKey1 = PrincipalKey.ofUser( SYSTEM, "User1" );
            final CreateUserParams createUser1 = CreateUserParams.create().
                userKey( userKey1 ).
                displayName( "User 1" ).
                email( "user1@enonic.com" ).
                login( "User1" ).
                password( "123456" ).
                build();
            final PrincipalKey userKey2 = PrincipalKey.ofUser( SYSTEM, "user2" );
            final CreateUserParams createUser2 = CreateUserParams.create().
                userKey( userKey2 ).
                displayName( "User 2" ).
                email( "user2@enonic.com" ).
                login( "user2" ).
                build();
            final PrincipalKey groupKey1 = PrincipalKey.ofGroup( SYSTEM, "Group-a" );
            final CreateGroupParams createGroup = CreateGroupParams.create().
                groupKey( groupKey1 ).
                displayName( "Group A" ).
                build();

            securityService.createUser( createUser1 );
            securityService.createUser( createUser2 );
            securityService.createGroup( createGroup );

            PrincipalRelationship membership = PrincipalRelationship.from( groupKey1 ).to( userKey1 );
            PrincipalRelationship membership2 = PrincipalRelationship.from( groupKey1 ).to( userKey2 );

            // exercise
            securityService.addRelationship( membership );
            securityService.addRelationship( membership2 );
            securityService.addRelationship( membership );
            refresh();

            // verify
            final PrincipalRelationships relationships = securityService.getRelationships( groupKey1 );
            assertEquals( 2, relationships.getSize() );
            assertEquals( membership, relationships.get( 0 ) );
            assertEquals( membership2, relationships.get( 1 ) );
        } );
    }

    @Test
    public void testRemoveRelationship()
        throws Exception
    {
        runAsAdmin( () -> {
            // set up
            final PrincipalKey userKey1 = PrincipalKey.ofUser( SYSTEM, "User1" );
            final CreateUserParams createUser1 = CreateUserParams.create().
                userKey( userKey1 ).
                displayName( "User 1" ).
                email( "user1@enonic.com" ).
                login( "User1" ).
                password( "123456" ).
                build();
            final PrincipalKey userKey2 = PrincipalKey.ofUser( SYSTEM, "user2" );
            final CreateUserParams createUser2 = CreateUserParams.create().
                userKey( userKey2 ).
                displayName( "User 2" ).
                email( "user2@enonic.com" ).
                login( "user2" ).
                build();
            final PrincipalKey groupKey1 = PrincipalKey.ofGroup( SYSTEM, "Group-a" );
            final CreateGroupParams createGroup = CreateGroupParams.create().
                groupKey( groupKey1 ).
                displayName( "Group A" ).
                build();

            securityService.createUser( createUser1 );
            securityService.createUser( createUser2 );
            securityService.createGroup( createGroup );
            refresh();

            PrincipalRelationship membership = PrincipalRelationship.from( groupKey1 ).to( userKey1 );
            PrincipalRelationship membership2 = PrincipalRelationship.from( groupKey1 ).to( userKey2 );

            securityService.addRelationship( membership );
            securityService.addRelationship( membership2 );
            refresh();

            // exercise
            securityService.removeRelationship( membership );
            refresh();

            //verify
            final PrincipalRelationships relationships = securityService.getRelationships( groupKey1 );
            assertEquals( 1, relationships.getSize() );
            assertEquals( membership2, relationships.get( 0 ) );
        } );
    }

    @Test
    public void testRemoveAllRelationships()
        throws Exception
    {
        runAsAdmin( () -> {
            // set up
            final PrincipalKey userKey1 = PrincipalKey.ofUser( SYSTEM, "User1" );
            final CreateUserParams createUser1 = CreateUserParams.create().
                userKey( userKey1 ).
                displayName( "User 1" ).
                email( "user1@enonic.com" ).
                login( "User1" ).
                password( "123456" ).
                build();
            final PrincipalKey userKey2 = PrincipalKey.ofUser( SYSTEM, "user2" );
            final CreateUserParams createUser2 = CreateUserParams.create().
                userKey( userKey2 ).
                displayName( "User 2" ).
                email( "user2@enonic.com" ).
                login( "user2" ).
                build();
            final PrincipalKey groupKey1 = PrincipalKey.ofGroup( SYSTEM, "Group-a" );
            final CreateGroupParams createGroup = CreateGroupParams.create().
                groupKey( groupKey1 ).
                displayName( "Group A" ).
                build();

            securityService.createUser( createUser1 );
            securityService.createUser( createUser2 );
            securityService.createGroup( createGroup );
            refresh();

            PrincipalRelationship membership = PrincipalRelationship.from( groupKey1 ).to( userKey1 );
            PrincipalRelationship membership2 = PrincipalRelationship.from( groupKey1 ).to( userKey2 );

            securityService.addRelationship( membership );
            securityService.addRelationship( membership2 );
            refresh();

            // exercise
            securityService.removeRelationships( groupKey1 );
            refresh();

            //verify
            final PrincipalRelationships relationships = securityService.getRelationships( groupKey1 );
            assertEquals( 0, relationships.getSize() );
        } );
    }

    @Test
    public void testAuthenticateByEmailPwd()
        throws Exception
    {
        runAsAdmin( () -> {
            final CreateUserParams createUser = CreateUserParams.create().
                userKey( PrincipalKey.ofUser( SYSTEM, "User1" ) ).
                displayName( "User 1" ).
                email( "user1@enonic.com" ).
                login( "User1" ).
                password( "password" ).
                build();

            final User user = securityService.createUser( createUser );
            refresh();

            final EmailPasswordAuthToken authToken = new EmailPasswordAuthToken();
            authToken.setEmail( "user1@enonic.com" );
            authToken.setPassword( "password" );
            authToken.setUserStore( SYSTEM );

            final AuthenticationInfo authInfo = securityService.authenticate( authToken );
            assertTrue( authInfo.isAuthenticated() );
            assertEquals( user.getKey(), authInfo.getUser().getKey() );
        } );
    }

    @Test
    public void testAuthenticateByEmailPwdWrongPwd()
        throws Exception
    {
        runAsAdmin( () -> {
            final CreateUserParams createUser = CreateUserParams.create().
                userKey( PrincipalKey.ofUser( SYSTEM, "User1" ) ).
                displayName( "User 1" ).
                email( "user1@enonic.com" ).
                login( "User1" ).
                password( "fisk" ).
                build();

            securityService.createUser( createUser );
            refresh();

            final EmailPasswordAuthToken authToken = new EmailPasswordAuthToken();
            authToken.setEmail( "user1@enonic.com" );
            authToken.setPassword( "password" );
            authToken.setUserStore( SYSTEM );

            final AuthenticationInfo authInfo = securityService.authenticate( authToken );
            assertFalse( authInfo.isAuthenticated() );
        } );
    }

    @Test
    public void testAuthenticateByUsernamePwd()
        throws Exception
    {
        runAsAdmin( () -> {
            final CreateUserParams createUser = CreateUserParams.create().
                userKey( PrincipalKey.ofUser( SYSTEM, "User1" ) ).
                displayName( "User 1" ).
                email( "user1@enonic.com" ).
                login( "User1" ).
                password( "runar" ).
                build();

            final User user = securityService.createUser( createUser );
            refresh();

            final UsernamePasswordAuthToken authToken = new UsernamePasswordAuthToken();
            authToken.setUsername( "User1" );
            authToken.setPassword( "runar" );
            authToken.setUserStore( SYSTEM );

            final AuthenticationInfo authInfo = securityService.authenticate( authToken );
            assertTrue( authInfo.isAuthenticated() );
            assertEquals( user.getKey(), authInfo.getUser().getKey() );
        } );
    }

    @Test
    public void testAuthenticateByEmail()
        throws Exception
    {
        runAsAdmin( () -> {
            final CreateUserParams createUser = CreateUserParams.create().
                userKey( PrincipalKey.ofUser( SYSTEM, "User1" ) ).
                displayName( "User 1" ).
                email( "user1@enonic.com" ).
                login( "User1" ).
                password( "password" ).
                build();

            final User user = securityService.createUser( createUser );
            refresh();

            final VerifiedEmailAuthToken authToken = new VerifiedEmailAuthToken();
            authToken.setEmail( "user1@enonic.com" );
            authToken.setUserStore( SYSTEM );

            final AuthenticationInfo authInfo = securityService.authenticate( authToken );
            assertTrue( authInfo.isAuthenticated() );
            assertEquals( user.getKey(), authInfo.getUser().getKey() );
        } );
    }

    @Test
    public void testAuthenticateByUsername()
        throws Exception
    {
        runAsAdmin( () -> {
            final CreateUserParams createUser = CreateUserParams.create().
                userKey( PrincipalKey.ofUser( SYSTEM, "User1" ) ).
                displayName( "User 1" ).
                email( "user1@enonic.com" ).
                login( "User1" ).
                password( "runar" ).
                build();

            final User user = securityService.createUser( createUser );
            refresh();

            final VerifiedUsernameAuthToken authToken = new VerifiedUsernameAuthToken();
            authToken.setUsername( "user1" );
            authToken.setUserStore( SYSTEM );

            final AuthenticationInfo authInfo = securityService.authenticate( authToken );
            assertTrue( authInfo.isAuthenticated() );
            assertEquals( user.getKey(), authInfo.getUser().getKey() );
        } );
    }

    @Test(expected = AuthenticationException.class)
    public void testAuthenticateUnsupportedToken()
        throws Exception
    {
        runAsAdmin( () -> {
            final CreateUserParams createUser = CreateUserParams.create().
                userKey( PrincipalKey.ofUser( SYSTEM, "User1" ) ).
                displayName( "User 1" ).
                email( "user1@enonic.com" ).
                login( "User1" ).
                build();

            final User user = securityService.createUser( createUser );
            refresh();

            final CustomAuthenticationToken authToken = new CustomAuthenticationToken();
            authToken.setUserStore( SYSTEM );

            final AuthenticationInfo authInfo = securityService.authenticate( authToken );
            assertEquals( user.getKey(), authInfo.getUser().getKey() );
        } );
    }

    @Test
    public void testGetUserMemberships()
        throws Exception
    {
        runAsAdmin( () -> {
            final PrincipalKey userKey = PrincipalKey.ofUser( SYSTEM, "User1" );
            final CreateUserParams createUser = CreateUserParams.create().
                userKey( userKey ).
                displayName( "User 1" ).
                email( "user1@enonic.com" ).
                login( "User1" ).
                password( "123456" ).
                build();

            final PrincipalKey groupKey1 = PrincipalKey.ofGroup( SYSTEM, "Group-a" );
            final CreateGroupParams createGroup1 = CreateGroupParams.create().
                groupKey( groupKey1 ).
                displayName( "Group A" ).
                build();

            final PrincipalKey groupKey2 = PrincipalKey.ofGroup( SYSTEM, "group-b" );
            final CreateGroupParams createGroup2 = CreateGroupParams.create().
                groupKey( groupKey2 ).
                displayName( "Group B" ).
                build();

            securityService.createUser( createUser );
            securityService.createGroup( createGroup1 );
            securityService.createGroup( createGroup2 );
            securityService.addRelationship( PrincipalRelationship.from( groupKey1 ).to( userKey ) );
            securityService.addRelationship( PrincipalRelationship.from( groupKey2 ).to( userKey ) );

            refresh();

            final PrincipalKeys memberships = securityService.getMemberships( userKey );

            assertTrue( memberships.contains( groupKey1 ) );
            assertTrue( memberships.contains( groupKey2 ) );
            assertEquals( 2, memberships.getSize() );
        } );
    }

    @Test
    public void testGetAllMemberships()
        throws Exception
    {
        runAsAdmin( () -> {
            final PrincipalKey userKey = PrincipalKey.ofUser( SYSTEM, "user1" );
            final CreateUserParams createUser = CreateUserParams.create().
                userKey( userKey ).
                displayName( "User 1" ).
                email( "user1@enonic.com" ).
                login( "user1" ).
                password( "123456" ).
                build();

            final PrincipalKey groupKey1 = PrincipalKey.ofGroup( SYSTEM, "group-a" );
            final CreateGroupParams createGroup1 = CreateGroupParams.create().
                groupKey( groupKey1 ).
                displayName( "Group A" ).
                build();

            final PrincipalKey groupKey2 = PrincipalKey.ofGroup( SYSTEM, "group-b" );
            final CreateGroupParams createGroup2 = CreateGroupParams.create().
                groupKey( groupKey2 ).
                displayName( "Group B" ).
                build();

            final PrincipalKey roleKey1 = PrincipalKey.ofRole( "role-a" );
            final CreateRoleParams createRole = CreateRoleParams.create().
                roleKey( roleKey1 ).
                displayName( "Role A" ).
                description( "Group A Description" ).
                build();

            securityService.createUser( createUser );
            securityService.createGroup( createGroup1 );
            securityService.createGroup( createGroup2 );
            securityService.createRole( createRole );
            securityService.addRelationship( PrincipalRelationship.from( groupKey1 ).to( userKey ) );
            securityService.addRelationship( PrincipalRelationship.from( groupKey2 ).to( groupKey1 ) );
            securityService.addRelationship( PrincipalRelationship.from( roleKey1 ).to( groupKey2 ) );

            refresh();

            final PrincipalKeys memberships = securityService.getAllMemberships( userKey );

            assertTrue( memberships.contains( groupKey1 ) );
            assertTrue( memberships.contains( groupKey2 ) );
            assertTrue( memberships.contains( roleKey1 ) );
            assertEquals( 3, memberships.getSize() );
        } );
    }

    @Test
    public void testCreateUserStore()
        throws Exception
    {
        runAsAdmin( () -> {
            final PrincipalKey userKey = PrincipalKey.ofUser( SYSTEM, "User1" );
            final PrincipalKey groupKey1 = PrincipalKey.ofGroup( SYSTEM, "group-a" );
            final PrincipalKey groupKey2 = PrincipalKey.ofGroup( SYSTEM, "group-b" );

            final UserStoreAccessControlList permissions =
                UserStoreAccessControlList.of( UserStoreAccessControlEntry.create().principal( userKey ).access( CREATE_USERS ).build(),
                                               UserStoreAccessControlEntry.create().principal( groupKey1 ).access( ADMINISTRATOR ).build(),
                                               UserStoreAccessControlEntry.create().principal( groupKey2 ).access( WRITE_USERS ).build() );
            final CreateUserStoreParams createUserStore = CreateUserStoreParams.create().
                key( UserStoreKey.from( "enonic" ) ).
                displayName( "Enonic User Store" ).
                permissions( permissions ).
                description( "user store description" ).
                build();

            final UserStore userStoreCreated = securityService.createUserStore( createUserStore );
            assertNotNull( userStoreCreated );
            assertEquals( "enonic", userStoreCreated.getKey().toString() );
            assertEquals( "Enonic User Store", userStoreCreated.getDisplayName() );
            assertEquals( "user store description", userStoreCreated.getDescription() );

            final UserStoreAccessControlList createdPermissions = securityService.getUserStorePermissions( UserStoreKey.from( "enonic" ) );
            assertNotNull( userStoreCreated );
            assertEquals( CREATE_USERS, createdPermissions.getEntry( userKey ).getAccess() );
            assertEquals( ADMINISTRATOR, createdPermissions.getEntry( groupKey1 ).getAccess() );
            assertEquals( WRITE_USERS, createdPermissions.getEntry( groupKey2 ).getAccess() );
        } );
    }

    @Test(expected = UserStoreAlreadyExistsException.class)
    public void testCreateUserStoreThrowsExceptionWhenNameIsOccupied()
        throws Exception
    {
        runAsAdmin( () -> {
            final PrincipalKey userKey = PrincipalKey.ofUser( SYSTEM, "User1" );

            final UserStoreAccessControlList permissions =
                UserStoreAccessControlList.of( UserStoreAccessControlEntry.create().principal( userKey ).access( CREATE_USERS ).build() );

            final CreateUserStoreParams createUserStore = CreateUserStoreParams.create().
                key( UserStoreKey.from( "enonic" ) ).
                displayName( "Enonic User Store" ).
                permissions( permissions ).
                description( "user store description" ).
                build();

            securityService.createUserStore( createUserStore );
            securityService.createUserStore( createUserStore );

        } );
    }

    @Test
    public void testUpdateUserStore()
        throws Exception
    {
        runAsAdmin( () -> {
            // setup
            final PrincipalKey userKey = PrincipalKey.ofUser( SYSTEM, "User1" );
            final PrincipalKey groupKey1 = PrincipalKey.ofGroup( SYSTEM, "Group-a" );
            final PrincipalKey groupKey2 = PrincipalKey.ofGroup( SYSTEM, "group-b" );

            final UserStoreAccessControlList permissions =
                UserStoreAccessControlList.of( UserStoreAccessControlEntry.create().principal( userKey ).access( CREATE_USERS ).build(),
                                               UserStoreAccessControlEntry.create().principal( groupKey1 ).access( ADMINISTRATOR ).build(),
                                               UserStoreAccessControlEntry.create().principal( groupKey2 ).access( WRITE_USERS ).build() );
            final CreateUserStoreParams createUserStore = CreateUserStoreParams.create().
                key( UserStoreKey.from( "enonic" ) ).
                displayName( "Enonic User Store" ).
                permissions( permissions ).
                description( "old user store description" ).
                build();
            final UserStore userStoreCreated = securityService.createUserStore( createUserStore );

            // exercise
            final UserStoreAccessControlList updatePermissions =
                UserStoreAccessControlList.of( UserStoreAccessControlEntry.create().principal( userKey ).access( CREATE_USERS ).build(),
                                               UserStoreAccessControlEntry.create().principal( groupKey1 ).access(
                                                   ADMINISTRATOR ).build() );
            final UpdateUserStoreParams updateUserStore = UpdateUserStoreParams.create().
                key( UserStoreKey.from( "enonic" ) ).
                displayName( "Enonic User Store updated" ).
                permissions( updatePermissions ).
                description( "new user store description" ).
                build();
            final UserStore userStoreUpdated = securityService.updateUserStore( updateUserStore );

            // verify
            assertNotNull( userStoreUpdated );
            assertEquals( "enonic", userStoreUpdated.getKey().toString() );
            assertEquals( "Enonic User Store updated", userStoreUpdated.getDisplayName() );
            assertEquals( "new user store description", userStoreUpdated.getDescription() );

            final UserStoreAccessControlList updatedPermissions = securityService.getUserStorePermissions( UserStoreKey.from( "enonic" ) );
            assertNotNull( userStoreCreated );
            assertEquals( CREATE_USERS, updatedPermissions.getEntry( userKey ).getAccess() );
            assertEquals( ADMINISTRATOR, updatedPermissions.getEntry( groupKey1 ).getAccess() );
            assertNull( updatedPermissions.getEntry( groupKey2 ) );
        } );
    }

    @Test
    public void testUpdateUserStoreWithEditor()
        throws Exception
    {
        runAsAdmin( () -> {
            // setup
            final CreateUserStoreParams createUserStore = CreateUserStoreParams.create().
                key( UserStoreKey.from( "enonic" ) ).
                displayName( "Enonic User Store" ).
                description( "old user store description" ).
                build();
            final UserStore userStoreCreated = securityService.createUserStore( createUserStore );

            // exercise
            final UpdateUserStoreParams updateUserStore = UpdateUserStoreParams.create( userStoreCreated ).
                editor( edit -> {
                    edit.key = UserStoreKey.from( "newEnonic" );
                    edit.displayName = "Enonic User Store updated";
                    edit.description = "new user store description";
                } ).
                displayName( "Display name from parameters" ).
                description( "Description from parameters" ).
                build();
            final UserStore userStoreUpdated = securityService.updateUserStore( updateUserStore );

            // verify
            assertNotNull( userStoreUpdated );
            assertEquals( "enonic", userStoreUpdated.getKey().toString() );
            assertEquals( "Enonic User Store updated", userStoreUpdated.getDisplayName() );
            assertEquals( "new user store description", userStoreUpdated.getDescription() );
        } );
    }

    @Test
    public void setPassword()
        throws Exception
    {
        runAsAdmin( () -> {
            final PrincipalKey userKey1 = PrincipalKey.ofUser( SYSTEM, "User1" );
            final CreateUserParams createUser1 = CreateUserParams.create().
                userKey( userKey1 ).
                displayName( "User 1" ).
                email( "user1@enonic.com" ).
                login( "User1" ).
                password( "fisk" ).
                build();

            final User user = securityService.createUser( createUser1 );
            refresh();

            final UsernamePasswordAuthToken authToken = new UsernamePasswordAuthToken();
            authToken.setUsername( "user1" );
            authToken.setPassword( "runar" );
            authToken.setUserStore( SYSTEM );

            AuthenticationInfo authInfo = securityService.authenticate( authToken );
            assertFalse( authInfo.isAuthenticated() );

            securityService.setPassword( user.getKey(), "runar" );

            AuthenticationInfo authInfo2 = securityService.authenticate( authToken );
            assertTrue( authInfo2.isAuthenticated() );
        } );
    }

    @Test
    public void testQuery()
        throws Exception
    {

        runAsAdmin( () -> {
            final PrincipalKey userKey1 = PrincipalKey.ofUser( SYSTEM, "User1" );

            final CreateUserParams createUser1 = CreateUserParams.create().
                userKey( userKey1 ).
                displayName( "User 1" ).
                email( "user1@enonic.com" ).
                login( "User1" ).
                password( "123456" ).
                build();

            refresh();

            final PrincipalQuery query = PrincipalQuery.create().userStore( UserStoreKey.system() ).build();
            PrincipalQueryResult queryResult = securityService.query( query );

            queryResult = securityService.query( query );
            assertEquals( 2, queryResult.getTotalSize() );

            final User user1 = securityService.createUser( createUser1 );

            refresh();

            queryResult = securityService.query( query );

            assertEquals( 3, queryResult.getTotalSize() );
            assertEquals( user1, queryResult.getPrincipals().getPrincipal( userKey1 ) );
        } );

    }

    private <T> T runAsAdmin( Callable<T> runnable )
    {
        return adminCtx().callWith( runnable );
    }

    private void runAsAdmin( Runnable runnable )
    {
        adminCtx().runWith( runnable );
    }

    private Context adminCtx()
    {
        final AuthenticationInfo authInfo = AuthenticationInfo.create().principals( RoleKeys.ADMIN ).user( User.ANONYMOUS ).build();

        return ContextBuilder.create().
            authInfo( authInfo ).
            repositoryId( SecurityConstants.SECURITY_REPO.getId() ).
            branch( SecurityConstants.BRANCH_SECURITY ).
            build();
    }

    private class CustomAuthenticationToken
        extends AuthenticationToken
    {
    }

}
