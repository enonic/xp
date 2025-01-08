package com.enonic.xp.core.security;

import java.util.Map;
import java.util.concurrent.Callable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.audit.AuditLogService;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.impl.audit.config.AuditLogConfig;
import com.enonic.xp.core.impl.security.SecurityAuditLogSupportImpl;
import com.enonic.xp.core.impl.security.SecurityConfig;
import com.enonic.xp.core.impl.security.SecurityInitializer;
import com.enonic.xp.core.impl.security.SecurityServiceImpl;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.internal.blobstore.MemoryBlobStore;
import com.enonic.xp.repo.impl.binary.BinaryServiceImpl;
import com.enonic.xp.repo.impl.branch.storage.BranchServiceImpl;
import com.enonic.xp.repo.impl.commit.CommitServiceImpl;
import com.enonic.xp.repo.impl.config.RepoConfigurationImpl;
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
import com.enonic.xp.repo.impl.repository.SystemRepoInitializer;
import com.enonic.xp.repo.impl.search.NodeSearchServiceImpl;
import com.enonic.xp.repo.impl.storage.IndexDataServiceImpl;
import com.enonic.xp.repo.impl.storage.NodeStorageServiceImpl;
import com.enonic.xp.repo.impl.version.VersionServiceImpl;
import com.enonic.xp.security.CreateGroupParams;
import com.enonic.xp.security.CreateIdProviderParams;
import com.enonic.xp.security.CreateRoleParams;
import com.enonic.xp.security.CreateUserParams;
import com.enonic.xp.security.Group;
import com.enonic.xp.security.IdProvider;
import com.enonic.xp.security.IdProviderAlreadyExistsException;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalAlreadyExistsException;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.PrincipalNotFoundException;
import com.enonic.xp.security.PrincipalQuery;
import com.enonic.xp.security.PrincipalQueryResult;
import com.enonic.xp.security.PrincipalRelationship;
import com.enonic.xp.security.PrincipalRelationships;
import com.enonic.xp.security.Role;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityConstants;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.security.UpdateGroupParams;
import com.enonic.xp.security.UpdateIdProviderParams;
import com.enonic.xp.security.UpdateRoleParams;
import com.enonic.xp.security.UpdateUserParams;
import com.enonic.xp.security.User;
import com.enonic.xp.security.acl.IdProviderAccessControlEntry;
import com.enonic.xp.security.acl.IdProviderAccessControlList;
import com.enonic.xp.security.auth.AuthenticationException;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.security.auth.AuthenticationToken;
import com.enonic.xp.security.auth.EmailPasswordAuthToken;
import com.enonic.xp.security.auth.UsernamePasswordAuthToken;
import com.enonic.xp.security.auth.VerifiedEmailAuthToken;
import com.enonic.xp.security.auth.VerifiedUsernameAuthToken;

import static com.enonic.xp.security.acl.IdProviderAccess.ADMINISTRATOR;
import static com.enonic.xp.security.acl.IdProviderAccess.CREATE_USERS;
import static com.enonic.xp.security.acl.IdProviderAccess.WRITE_USERS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

public class SecurityServiceImplTest
    extends AbstractElasticsearchIntegrationTest
{
    private static final IdProviderKey SYSTEM = IdProviderKey.system();

    protected EventPublisher eventPublisher;

    private SecurityServiceImpl securityService;

    private NodeServiceImpl nodeService;

    @BeforeEach
    void setUp()
        throws Exception
    {
        deleteAllIndices();
        final MemoryBlobStore blobStore = new MemoryBlobStore();

        final BinaryServiceImpl binaryService = new BinaryServiceImpl( blobStore );

        final StorageDaoImpl storageDao = new StorageDaoImpl( client );

        final SearchDaoImpl searchDao = new SearchDaoImpl( client );

        final BranchServiceImpl branchService = new BranchServiceImpl( storageDao, searchDao );

        final VersionServiceImpl versionService = new VersionServiceImpl( storageDao );

        final CommitServiceImpl commitService = new CommitServiceImpl( storageDao );

        final NodeVersionServiceImpl nodeDao = new NodeVersionServiceImpl( blobStore, new RepoConfigurationImpl( Map.of() ) );

        IndexServiceInternalImpl indexServiceInternal = new IndexServiceInternalImpl( client );

        final NodeSearchServiceImpl searchService = new NodeSearchServiceImpl( searchDao );

        IndexDataServiceImpl indexedDataService = new IndexDataServiceImpl( storageDao );

        final NodeStorageServiceImpl storageService =
            new NodeStorageServiceImpl( versionService, branchService, commitService, nodeDao, indexedDataService );

        final NodeRepositoryServiceImpl nodeRepositoryService = new NodeRepositoryServiceImpl( indexServiceInternal );

        this.eventPublisher = mock( EventPublisher.class );

        final RepositoryEntryServiceImpl repositoryEntryService =
            new RepositoryEntryServiceImpl( indexServiceInternal, storageService, searchService, eventPublisher, binaryService );

        RepositoryServiceImpl repositoryService =
            new RepositoryServiceImpl( repositoryEntryService, indexServiceInternal, nodeRepositoryService, storageService, searchService );
        SystemRepoInitializer.create().
            setIndexServiceInternal( indexServiceInternal ).
            setRepositoryService( repositoryService ).
            setNodeStorageService( storageService ).
            build().
            initialize();

        this.nodeService =
            new NodeServiceImpl( indexServiceInternal, storageService, searchService, eventPublisher, binaryService, repositoryService );

        IndexServiceImpl indexService = new IndexServiceImpl( indexServiceInternal, indexedDataService, searchService, nodeDao, repositoryEntryService );

        AuditLogConfig auditLogConfig = mock( AuditLogConfig.class );
        Mockito.when( auditLogConfig.isEnabled() ).thenReturn( true );
        Mockito.when( auditLogConfig.isOutputLogs() ).thenReturn( true );

        AuditLogService auditLogService = mock( AuditLogService.class, withSettings().stubOnly() );

        SecurityConfig securityConfig = mock( SecurityConfig.class );
        Mockito.when( securityConfig.auditlog_enabled() ).thenReturn( true );

        SecurityAuditLogSupportImpl securityAuditLogSupport = new SecurityAuditLogSupportImpl( auditLogService );
        securityAuditLogSupport.activate( securityConfig );

        securityService = new SecurityServiceImpl( this.nodeService, securityAuditLogSupport );

        runAsAdmin( () -> SecurityInitializer.create()
            .setIndexService( indexService )
            .setSecurityService( securityService )
            .setNodeService( nodeService )
            .build()
            .initialize() );

    }

    @Test
    public void testCreateUser()
        throws Exception
    {
        runAsAdmin( () -> {
            final PrincipalKey userKey1 = PrincipalKey.ofUser( SYSTEM, "User1" );
            final CreateUserParams createUser1 = CreateUserParams.create()
                .userKey( userKey1 )
                .displayName( "User 1" )
                .email( "user1@enonic.com" )
                .login( "User1" )
                .password( "123456" )
                .build();

            final PrincipalKey userKey2 = PrincipalKey.ofUser( SYSTEM, "user2" );
            final CreateUserParams createUser2 =
                CreateUserParams.create().userKey( userKey2 ).displayName( "User 2" ).email( "user2@enonic.com" ).login( "user2" ).build();

            final User user1 = securityService.createUser( createUser1 );
            final User user2 = securityService.createUser( createUser2 );

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

    @Test
    public void testCreateUserThrowsExceptionWhenNameIsOccupied()
        throws Exception
    {
        assertThrows( PrincipalAlreadyExistsException.class, () -> runAsAdmin( () -> {
            final PrincipalKey userKey1 = PrincipalKey.ofUser( SYSTEM, "User1" );
            final CreateUserParams createUser1 = CreateUserParams.create()
                .userKey( userKey1 )
                .displayName( "User 1" )
                .email( "user1@enonic.com" )
                .login( "User1" )
                .password( "123456" )
                .build();

            securityService.createUser( createUser1 );
            securityService.createUser( createUser1 );
        } ) );
    }

    @Test
    public void testCreateUserDuplicatedEmail()
    {
        try
        {
            runAsAdmin( () -> {
                final PrincipalKey userKey1 = PrincipalKey.ofUser( SYSTEM, "User1" );
                final CreateUserParams createUser1 = CreateUserParams.create()
                    .userKey( userKey1 )
                    .displayName( "User 1" )
                    .email( "same_email@enonic.com" )
                    .login( "User1" )
                    .password( "123456" )
                    .build();

                final PrincipalKey userKey2 = PrincipalKey.ofUser( SYSTEM, "user2" );
                final CreateUserParams createUser2 = CreateUserParams.create()
                    .userKey( userKey2 )
                    .displayName( "User 2" )
                    .email( "same_email@enonic.com" )
                    .login( "user2" )
                    .build();

                securityService.createUser( createUser1 );
                securityService.createUser( createUser2 );
            } );

            fail( "Expected exception" );
        }
        catch ( IllegalArgumentException e )
        {
            assertEquals( "A user with email 'same_email@enonic.com' already exists in id provider 'system'", e.getMessage() );
        }
    }

    @Test
    public void testUpdateUser()
        throws Exception
    {
        runAsAdmin( () -> {
            final CreateUserParams createUser = CreateUserParams.create()
                .userKey( PrincipalKey.ofUser( SYSTEM, "User1" ) )
                .displayName( "User 1" )
                .email( "user1@enonic.com" )
                .login( "User1" )
                .build();

            final User user = securityService.createUser( createUser );

            final UpdateUserParams updateUserParams = UpdateUserParams.create( user ).email( "u2@enonic.net" ).build();
            final User updateUserResult = securityService.updateUser( updateUserParams );

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
                final CreateUserParams createUser1 = CreateUserParams.create()
                    .userKey( userKey1 )
                    .displayName( "User 1" )
                    .email( "same_email@enonic.com" )
                    .login( "User1" )
                    .password( "123456" )
                    .build();

                final PrincipalKey userKey2 = PrincipalKey.ofUser( SYSTEM, "user2" );
                final CreateUserParams createUser2 = CreateUserParams.create()
                    .userKey( userKey2 )
                    .displayName( "User 2" )
                    .email( "same_email@enonic.com" )
                    .login( "user2" )
                    .build();

                final User user1 = securityService.createUser( createUser1 );
                securityService.createUser( createUser2 );

                final UpdateUserParams updateUserParams = UpdateUserParams.create( user1 ).email( "same_email@enonic.com" ).build();
                securityService.updateUser( updateUserParams );
            } );

            fail( "Expected exception" );
        }
        catch ( IllegalArgumentException e )
        {
            assertEquals( "A user with email 'same_email@enonic.com' already exists in id provider 'system'", e.getMessage() );
        }
    }

    @Test
    public void testUpdateUserDuplicatedEmailWithEditor()
    {
        try
        {
            runAsAdmin( () -> {
                final PrincipalKey userKey1 = PrincipalKey.ofUser( SYSTEM, "User1" );
                final CreateUserParams createUser1 = CreateUserParams.create()
                    .userKey( userKey1 )
                    .displayName( "User 1" )
                    .email( "same_email@enonic.com" )
                    .login( "User1" )
                    .password( "123456" )
                    .build();

                final PrincipalKey userKey2 = PrincipalKey.ofUser( SYSTEM, "user2" );
                final CreateUserParams createUser2 = CreateUserParams.create()
                    .userKey( userKey2 )
                    .displayName( "User 2" )
                    .email( "same_email@enonic.com" )
                    .login( "user2" )
                    .build();

                final User user1 = securityService.createUser( createUser1 );
                securityService.createUser( createUser2 );

                final UpdateUserParams updateUserParams =
                    UpdateUserParams.create( user1 ).editor( editableUser -> editableUser.email = "same_email@enonic.com" ).build();
                securityService.updateUser( updateUserParams );
            } );

            fail( "Expected exception" );
        }
        catch ( IllegalArgumentException e )
        {
            assertEquals( "A user with email 'same_email@enonic.com' already exists in id provider 'system'", e.getMessage() );
        }
    }

    @Test
    public void testCreateGroup()
        throws Exception
    {
        runAsAdmin( () -> {
            final PrincipalKey groupKey1 = PrincipalKey.ofGroup( SYSTEM, "Group-a" );
            final CreateGroupParams createGroup =
                CreateGroupParams.create().groupKey( groupKey1 ).displayName( "Group A" ).description( "Group A Description" ).build();

            final PrincipalKey groupKey2 = PrincipalKey.ofGroup( SYSTEM, "group-b" );
            final CreateGroupParams createGroup2 = CreateGroupParams.create().groupKey( groupKey2 ).displayName( "Group B" ).build();

            final Group group1 = securityService.createGroup( createGroup );
            final Group group2 = securityService.createGroup( createGroup2 );

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

    @Test
    public void testCreateGroupThrowsExceptionWhenNameIsOccupied()
        throws Exception
    {
        assertThrows( PrincipalAlreadyExistsException.class, () -> runAsAdmin( () -> {
            final PrincipalKey groupKey1 = PrincipalKey.ofGroup( SYSTEM, "Group-a" );
            final CreateGroupParams createGroup =
                CreateGroupParams.create().groupKey( groupKey1 ).displayName( "Group A" ).description( "Group A Description" ).build();

            securityService.createGroup( createGroup );
            securityService.createGroup( createGroup );
        } ) );
    }

    @Test
    public void testUpdateGroup()
        throws Exception
    {
        runAsAdmin( () -> {
            final CreateGroupParams createGroup =
                CreateGroupParams.create().groupKey( PrincipalKey.ofGroup( SYSTEM, "Group-a" ) ).displayName( "Group A" ).build();

            final Group group = securityService.createGroup( createGroup );

            final UpdateGroupParams groupUpdate =
                UpdateGroupParams.create( group ).displayName( "___Group B___" ).description( "description" ).build();
            final Group updatedGroupResult = securityService.updateGroup( groupUpdate );

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
            final CreateRoleParams createRole =
                CreateRoleParams.create().roleKey( roleKey1 ).displayName( "Role A" ).description( "Group A Description" ).build();

            final PrincipalKey roleKey2 = PrincipalKey.ofRole( "role-b" );
            final CreateRoleParams createRole2 = CreateRoleParams.create().roleKey( roleKey2 ).displayName( "Role B" ).build();

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

    @Test
    public void testCreateRoleThrowsExceptionWhenNameIsOccupied()
        throws Exception
    {
        assertThrows( PrincipalAlreadyExistsException.class, () -> runAsAdmin( () -> {
            final PrincipalKey roleKey1 = PrincipalKey.ofRole( "Role-a" );
            final CreateRoleParams createRole =
                CreateRoleParams.create().roleKey( roleKey1 ).displayName( "Role A" ).description( "Group A Description" ).build();

            securityService.createRole( createRole );
            securityService.createRole( createRole );
        } ) );
    }

    @Test
    public void testUpdateRole()
        throws Exception
    {
        runAsAdmin( () -> {
            final CreateRoleParams createRole =
                CreateRoleParams.create().roleKey( PrincipalKey.ofRole( "Role-a" ) ).displayName( "Role A" ).build();

            final Role role = securityService.createRole( createRole );

            final UpdateRoleParams roleUpdate =
                UpdateRoleParams.create( role ).displayName( "___Role B___" ).description( "description" ).build();
            final Role updatedRoleResult = securityService.updateRole( roleUpdate );

            final Role updatedRole = securityService.getRole( role.getKey() ).get();
            assertEquals( "___Role B___", updatedRoleResult.getDisplayName() );
            assertEquals( "___Role B___", updatedRole.getDisplayName() );
            assertEquals( "description", updatedRoleResult.getDescription() );
            assertEquals( "description", updatedRole.getDescription() );
        } );
    }

    @Test
    public void testDeleteNonExistPrincipal()
        throws Exception
    {
        runAsAdmin( () -> {
            assertThrows( PrincipalNotFoundException.class,
                          () -> securityService.deletePrincipal( PrincipalKey.from( "user:system:invalid" ) ) );
        } );
    }

    @Test
    public void testDeletePrincipalWithoutPermissions()
        throws Exception
    {
        final CreateUserParams createUser = CreateUserParams.create()
            .userKey( PrincipalKey.ofUser( SYSTEM, "User1" ) )
            .displayName( "User 1" )
            .email( "user1@enonic.com" )
            .login( "User1" )
            .build();

        runAsAdmin( () -> {
            securityService.createUser( createUser );
        } );

        assertThrows( PrincipalNotFoundException.class, () -> securityService.deletePrincipal( createUser.getKey() ) );
    }

    @Test
    public void testAddRelationship()
        throws Exception
    {
        runAsAdmin( () -> {
            // set up
            final PrincipalKey userKey1 = PrincipalKey.ofUser( SYSTEM, "User1" );
            final CreateUserParams createUser1 = CreateUserParams.create()
                .userKey( userKey1 )
                .displayName( "User 1" )
                .email( "user1@enonic.com" )
                .login( "User1" )
                .password( "123456" )
                .build();
            final PrincipalKey userKey2 = PrincipalKey.ofUser( SYSTEM, "user2" );
            final CreateUserParams createUser2 =
                CreateUserParams.create().userKey( userKey2 ).displayName( "User 2" ).email( "user2@enonic.com" ).login( "user2" ).build();
            final PrincipalKey groupKey1 = PrincipalKey.ofGroup( SYSTEM, "Group-a" );
            final CreateGroupParams createGroup = CreateGroupParams.create().groupKey( groupKey1 ).displayName( "Group A" ).build();

            securityService.createUser( createUser1 );
            securityService.createUser( createUser2 );
            securityService.createGroup( createGroup );

            PrincipalRelationship membership = PrincipalRelationship.from( groupKey1 ).to( userKey1 );
            PrincipalRelationship membership2 = PrincipalRelationship.from( groupKey1 ).to( userKey2 );

            // exercise
            securityService.addRelationship( membership );
            securityService.addRelationship( membership2 );
            securityService.addRelationship( membership );

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
            final CreateUserParams createUser1 = CreateUserParams.create()
                .userKey( userKey1 )
                .displayName( "User 1" )
                .email( "user1@enonic.com" )
                .login( "User1" )
                .password( "123456" )
                .build();
            final PrincipalKey userKey2 = PrincipalKey.ofUser( SYSTEM, "user2" );
            final CreateUserParams createUser2 =
                CreateUserParams.create().userKey( userKey2 ).displayName( "User 2" ).email( "user2@enonic.com" ).login( "user2" ).build();
            final PrincipalKey groupKey1 = PrincipalKey.ofGroup( SYSTEM, "Group-a" );
            final CreateGroupParams createGroup = CreateGroupParams.create().groupKey( groupKey1 ).displayName( "Group A" ).build();

            securityService.createUser( createUser1 );
            securityService.createUser( createUser2 );
            securityService.createGroup( createGroup );

            PrincipalRelationship membership = PrincipalRelationship.from( groupKey1 ).to( userKey1 );
            PrincipalRelationship membership2 = PrincipalRelationship.from( groupKey1 ).to( userKey2 );

            securityService.addRelationship( membership );
            securityService.addRelationship( membership2 );

            // exercise
            securityService.removeRelationship( membership );

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
            final CreateUserParams createUser1 = CreateUserParams.create()
                .userKey( userKey1 )
                .displayName( "User 1" )
                .email( "user1@enonic.com" )
                .login( "User1" )
                .password( "123456" )
                .build();
            final PrincipalKey userKey2 = PrincipalKey.ofUser( SYSTEM, "user2" );
            final CreateUserParams createUser2 =
                CreateUserParams.create().userKey( userKey2 ).displayName( "User 2" ).email( "user2@enonic.com" ).login( "user2" ).build();
            final PrincipalKey groupKey1 = PrincipalKey.ofGroup( SYSTEM, "Group-a" );
            final CreateGroupParams createGroup = CreateGroupParams.create().groupKey( groupKey1 ).displayName( "Group A" ).build();

            securityService.createUser( createUser1 );
            securityService.createUser( createUser2 );
            securityService.createGroup( createGroup );

            PrincipalRelationship membership = PrincipalRelationship.from( groupKey1 ).to( userKey1 );
            PrincipalRelationship membership2 = PrincipalRelationship.from( groupKey1 ).to( userKey2 );

            securityService.addRelationship( membership );
            securityService.addRelationship( membership2 );

            // exercise
            securityService.removeRelationships( groupKey1 );

            //verify
            final PrincipalRelationships relationships = securityService.getRelationships( groupKey1 );
            assertEquals( 0, relationships.getSize() );
        } );
    }

    @Test
    public void testRemoveSuFromAdmin()
        throws Exception
    {
        runAsAdmin( () -> {
            assertThrows( IllegalArgumentException.class, () -> securityService.removeRelationship(
                PrincipalRelationship.from( RoleKeys.ADMIN ).to( PrincipalKey.ofSuperUser() ) ) );
        } );
    }

    @Test
    public void testDeleteAdminRole()
        throws Exception
    {
        runAsAdmin( () -> {
            assertThrows( IllegalArgumentException.class, () -> securityService.deletePrincipal( RoleKeys.ADMIN ) );
        } );
    }

    @Test
    public void testDeleteSu()
        throws Exception
    {
        runAsAdmin( () -> {
            assertThrows( IllegalArgumentException.class, () -> securityService.deletePrincipal( PrincipalKey.ofSuperUser() ) );
        } );
    }


    @Test
    public void testAuthenticateByEmailPwd()
        throws Exception
    {
        runAsAdmin( () -> {
            final CreateUserParams createUser = CreateUserParams.create()
                .userKey( PrincipalKey.ofUser( SYSTEM, "User1" ) )
                .displayName( "User 1" )
                .email( "user1@enonic.com" )
                .login( "User1" )
                .password( "password" )
                .build();

            final User user = securityService.createUser( createUser );

            final EmailPasswordAuthToken authToken = new EmailPasswordAuthToken();
            authToken.setEmail( "user1@enonic.com" );
            authToken.setPassword( "password" );
            authToken.setIdProvider( SYSTEM );

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
            final CreateUserParams createUser = CreateUserParams.create()
                .userKey( PrincipalKey.ofUser( SYSTEM, "User1" ) )
                .displayName( "User 1" )
                .email( "user1@enonic.com" )
                .login( "User1" )
                .password( "fisk" )
                .build();

            securityService.createUser( createUser );

            final EmailPasswordAuthToken authToken = new EmailPasswordAuthToken();
            authToken.setEmail( "user1@enonic.com" );
            authToken.setPassword( "password" );
            authToken.setIdProvider( SYSTEM );

            final AuthenticationInfo authInfo = securityService.authenticate( authToken );
            assertFalse( authInfo.isAuthenticated() );
        } );
    }

    @Test
    public void testAuthenticateByUsernamePwd()
        throws Exception
    {
        runAsAdmin( () -> {
            final CreateUserParams createUser = CreateUserParams.create()
                .userKey( PrincipalKey.ofUser( SYSTEM, "User1" ) )
                .displayName( "User 1" )
                .email( "user1@enonic.com" )
                .login( "User1" )
                .password( "runar" )
                .build();

            final User user = securityService.createUser( createUser );

            final UsernamePasswordAuthToken authToken = new UsernamePasswordAuthToken();
            authToken.setUsername( "User1" );
            authToken.setPassword( "runar" );
            authToken.setIdProvider( SYSTEM );

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
            final CreateUserParams createUser = CreateUserParams.create()
                .userKey( PrincipalKey.ofUser( SYSTEM, "User1" ) )
                .displayName( "User 1" )
                .email( "user1@enonic.com" )
                .login( "User1" )
                .password( "password" )
                .build();

            final User user = securityService.createUser( createUser );

            final VerifiedEmailAuthToken authToken = new VerifiedEmailAuthToken();
            authToken.setEmail( "user1@enonic.com" );
            authToken.setIdProvider( SYSTEM );

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
            final CreateUserParams createUser = CreateUserParams.create()
                .userKey( PrincipalKey.ofUser( SYSTEM, "User1" ) )
                .displayName( "User 1" )
                .email( "user1@enonic.com" )
                .login( "User1" )
                .password( "runar" )
                .build();

            final User user = securityService.createUser( createUser );

            final VerifiedUsernameAuthToken authToken = new VerifiedUsernameAuthToken();
            authToken.setUsername( "user1" );
            authToken.setIdProvider( SYSTEM );

            final AuthenticationInfo authInfo = securityService.authenticate( authToken );
            assertTrue( authInfo.isAuthenticated() );
            assertEquals( user.getKey(), authInfo.getUser().getKey() );
        } );
    }

    @Test
    public void testAuthenticateUnsupportedToken()
        throws Exception
    {
        assertThrows( AuthenticationException.class, () -> runAsAdmin( () -> {
            final CreateUserParams createUser = CreateUserParams.create()
                .userKey( PrincipalKey.ofUser( SYSTEM, "User1" ) )
                .displayName( "User 1" )
                .email( "user1@enonic.com" )
                .login( "User1" )
                .build();

            final User user = securityService.createUser( createUser );

            final CustomAuthenticationToken authToken = new CustomAuthenticationToken();
            authToken.setIdProvider( SYSTEM );

            final AuthenticationInfo authInfo = securityService.authenticate( authToken );
            assertEquals( user.getKey(), authInfo.getUser().getKey() );
        } ) );
    }

    @Test
    public void testGetUserMemberships()
        throws Exception
    {
        runAsAdmin( () -> {
            final PrincipalKey userKey = PrincipalKey.ofUser( SYSTEM, "User1" );
            final CreateUserParams createUser = CreateUserParams.create()
                .userKey( userKey )
                .displayName( "User 1" )
                .email( "user1@enonic.com" )
                .login( "User1" )
                .password( "123456" )
                .build();

            final PrincipalKey groupKey1 = PrincipalKey.ofGroup( SYSTEM, "Group-a" );
            final CreateGroupParams createGroup1 = CreateGroupParams.create().groupKey( groupKey1 ).displayName( "Group A" ).build();

            final PrincipalKey groupKey2 = PrincipalKey.ofGroup( SYSTEM, "group-b" );
            final CreateGroupParams createGroup2 = CreateGroupParams.create().groupKey( groupKey2 ).displayName( "Group B" ).build();

            securityService.createUser( createUser );
            securityService.createGroup( createGroup1 );
            securityService.createGroup( createGroup2 );
            securityService.addRelationship( PrincipalRelationship.from( groupKey1 ).to( userKey ) );
            securityService.addRelationship( PrincipalRelationship.from( groupKey2 ).to( userKey ) );

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
            final CreateUserParams createUser = CreateUserParams.create()
                .userKey( userKey )
                .displayName( "User 1" )
                .email( "user1@enonic.com" )
                .login( "user1" )
                .password( "123456" )
                .build();

            final PrincipalKey groupKey1 = PrincipalKey.ofGroup( SYSTEM, "group-a" );
            final CreateGroupParams createGroup1 = CreateGroupParams.create().groupKey( groupKey1 ).displayName( "Group A" ).build();

            final PrincipalKey groupKey2 = PrincipalKey.ofGroup( SYSTEM, "group-b" );
            final CreateGroupParams createGroup2 = CreateGroupParams.create().groupKey( groupKey2 ).displayName( "Group B" ).build();

            final PrincipalKey roleKey1 = PrincipalKey.ofRole( "role-a" );
            final CreateRoleParams createRole =
                CreateRoleParams.create().roleKey( roleKey1 ).displayName( "Role A" ).description( "Group A Description" ).build();

            securityService.createUser( createUser );
            securityService.createGroup( createGroup1 );
            securityService.createGroup( createGroup2 );
            securityService.createRole( createRole );
            securityService.addRelationship( PrincipalRelationship.from( groupKey1 ).to( userKey ) );
            securityService.addRelationship( PrincipalRelationship.from( groupKey2 ).to( groupKey1 ) );
            securityService.addRelationship( PrincipalRelationship.from( roleKey1 ).to( groupKey2 ) );

            final PrincipalKeys memberships = securityService.getAllMemberships( userKey );

            assertTrue( memberships.contains( groupKey1 ) );
            assertTrue( memberships.contains( groupKey2 ) );
            assertTrue( memberships.contains( roleKey1 ) );
            assertEquals( 3, memberships.getSize() );
        } );
    }

    @Test
    public void testCreateIdProvider()
        throws Exception
    {
        runAsAdmin( () -> {
            final PrincipalKey userKey = PrincipalKey.ofUser( SYSTEM, "User1" );
            final PrincipalKey groupKey1 = PrincipalKey.ofGroup( SYSTEM, "group-a" );
            final PrincipalKey groupKey2 = PrincipalKey.ofGroup( SYSTEM, "group-b" );

            final IdProviderAccessControlList permissions =
                IdProviderAccessControlList.of( IdProviderAccessControlEntry.create().principal( userKey ).access( CREATE_USERS ).build(),
                                                IdProviderAccessControlEntry.create()
                                                    .principal( groupKey1 )
                                                    .access( ADMINISTRATOR )
                                                    .build(), IdProviderAccessControlEntry.create()
                                                    .principal( groupKey2 )
                                                    .access( WRITE_USERS )
                                                    .build() );
            final CreateIdProviderParams createIdProvider = CreateIdProviderParams.create()
                .key( IdProviderKey.from( "enonic" ) )
                .displayName( "Enonic Id Provider" )
                .permissions( permissions )
                .description( "id provider description" )
                .build();

            final IdProvider idProviderCreated = securityService.createIdProvider( createIdProvider );
            assertNotNull( idProviderCreated );
            assertEquals( "enonic", idProviderCreated.getKey().toString() );
            assertEquals( "Enonic Id Provider", idProviderCreated.getDisplayName() );
            assertEquals( "id provider description", idProviderCreated.getDescription() );

            final IdProviderAccessControlList createdPermissions =
                securityService.getIdProviderPermissions( IdProviderKey.from( "enonic" ) );
            assertNotNull( idProviderCreated );
            assertEquals( CREATE_USERS, createdPermissions.getEntry( userKey ).getAccess() );
            assertEquals( ADMINISTRATOR, createdPermissions.getEntry( groupKey1 ).getAccess() );
            assertEquals( WRITE_USERS, createdPermissions.getEntry( groupKey2 ).getAccess() );
        } );
    }

    @Test
    void testDeleteIdProvider()
    {
        runAsAdmin( () -> {
            final PrincipalKey userKey = PrincipalKey.ofUser( SYSTEM, "User1" );
            final PrincipalKey groupKey1 = PrincipalKey.ofGroup( SYSTEM, "group-a" );
            final PrincipalKey groupKey2 = PrincipalKey.ofGroup( SYSTEM, "group-b" );

            final IdProviderAccessControlList permissions =
                IdProviderAccessControlList.of( IdProviderAccessControlEntry.create().principal( userKey ).access( CREATE_USERS ).build(),
                                                IdProviderAccessControlEntry.create()
                                                    .principal( groupKey1 )
                                                    .access( ADMINISTRATOR )
                                                    .build(), IdProviderAccessControlEntry.create()
                                                    .principal( groupKey2 )
                                                    .access( WRITE_USERS )
                                                    .build() );
            final CreateIdProviderParams createIdProvider = CreateIdProviderParams.create()
                .key( IdProviderKey.from( "enonic" ) )
                .displayName( "Enonic Id Provider" )
                .permissions( permissions )
                .description( "id provider description" )
                .build();

            final IdProvider idProviderCreated = securityService.createIdProvider( createIdProvider );


            securityService.deleteIdProvider( IdProviderKey.from( "enonic" ) );

            assertNull( securityService.getIdProvider( IdProviderKey.from( "enonic" ) ) );
        } );
    }

    @Test
    public void testCreateIdProviderThrowsExceptionWhenNameIsOccupied()
        throws Exception
    {
        assertThrows( IdProviderAlreadyExistsException.class, () -> runAsAdmin( () -> {
            final PrincipalKey userKey = PrincipalKey.ofUser( SYSTEM, "User1" );

            final IdProviderAccessControlList permissions =
                IdProviderAccessControlList.of( IdProviderAccessControlEntry.create().principal( userKey ).access( CREATE_USERS ).build() );

            final CreateIdProviderParams createIdProvider = CreateIdProviderParams.create()
                .key( IdProviderKey.from( "enonic" ) )
                .displayName( "Enonic Id Provider" )
                .permissions( permissions )
                .description( "id provider description" )
                .build();

            securityService.createIdProvider( createIdProvider );
            securityService.createIdProvider( createIdProvider );

        } ) );
    }

    @Test
    public void testUpdateIdProvider()
        throws Exception
    {
        runAsAdmin( () -> {
            // setup
            final PrincipalKey userKey = PrincipalKey.ofUser( SYSTEM, "User1" );
            final PrincipalKey groupKey1 = PrincipalKey.ofGroup( SYSTEM, "Group-a" );
            final PrincipalKey groupKey2 = PrincipalKey.ofGroup( SYSTEM, "group-b" );

            final IdProviderAccessControlList permissions =
                IdProviderAccessControlList.of( IdProviderAccessControlEntry.create().principal( userKey ).access( CREATE_USERS ).build(),
                                                IdProviderAccessControlEntry.create()
                                                    .principal( groupKey1 )
                                                    .access( ADMINISTRATOR )
                                                    .build(), IdProviderAccessControlEntry.create()
                                                    .principal( groupKey2 )
                                                    .access( WRITE_USERS )
                                                    .build() );
            final CreateIdProviderParams createIdProvider = CreateIdProviderParams.create()
                .key( IdProviderKey.from( "enonic" ) )
                .displayName( "Enonic Id Provider" )
                .permissions( permissions )
                .description( "old id provider description" )
                .build();
            final IdProvider idProviderCreated = securityService.createIdProvider( createIdProvider );

            // exercise
            final IdProviderAccessControlList updatePermissions =
                IdProviderAccessControlList.of( IdProviderAccessControlEntry.create().principal( userKey ).access( CREATE_USERS ).build(),
                                                IdProviderAccessControlEntry.create()
                                                    .principal( groupKey1 )
                                                    .access( ADMINISTRATOR )
                                                    .build() );
            final UpdateIdProviderParams updateIdProvider = UpdateIdProviderParams.create()
                .key( IdProviderKey.from( "enonic" ) )
                .displayName( "Enonic Id Provider updated" )
                .permissions( updatePermissions )
                .description( "new id provider description" )
                .build();
            final IdProvider idProviderUpdated = securityService.updateIdProvider( updateIdProvider );

            // verify
            assertNotNull( idProviderUpdated );
            assertEquals( "enonic", idProviderUpdated.getKey().toString() );
            assertEquals( "Enonic Id Provider updated", idProviderUpdated.getDisplayName() );
            assertEquals( "new id provider description", idProviderUpdated.getDescription() );

            final IdProviderAccessControlList updatedPermissions =
                securityService.getIdProviderPermissions( IdProviderKey.from( "enonic" ) );
            assertNotNull( idProviderCreated );
            assertEquals( CREATE_USERS, updatedPermissions.getEntry( userKey ).getAccess() );
            assertEquals( ADMINISTRATOR, updatedPermissions.getEntry( groupKey1 ).getAccess() );
            assertNull( updatedPermissions.getEntry( groupKey2 ) );
        } );
    }

    @Test
    public void testUpdateIdProviderWithEditor()
        throws Exception
    {
        runAsAdmin( () -> {
            // setup
            final CreateIdProviderParams createIdProvider = CreateIdProviderParams.create()
                .key( IdProviderKey.from( "enonic" ) )
                .displayName( "Enonic Id Provider" )
                .description( "old id provider description" )
                .build();
            final IdProvider idProviderCreated = securityService.createIdProvider( createIdProvider );

            // exercise
            final UpdateIdProviderParams updateIdProvider = UpdateIdProviderParams.create( idProviderCreated ).editor( edit -> {
                edit.key = IdProviderKey.from( "newEnonic" );
                edit.displayName = "Enonic Id Provider updated";
                edit.description = "new id provider description";
            } ).displayName( "Display name from parameters" ).description( "Description from parameters" ).build();
            final IdProvider idProviderUpdated = securityService.updateIdProvider( updateIdProvider );

            // verify
            assertNotNull( idProviderUpdated );
            assertEquals( "enonic", idProviderUpdated.getKey().toString() );
            assertEquals( "Enonic Id Provider updated", idProviderUpdated.getDisplayName() );
            assertEquals( "new id provider description", idProviderUpdated.getDescription() );
        } );
    }

    @Test
    public void setPassword()
        throws Exception
    {
        runAsAdmin( () -> {
            final PrincipalKey userKey1 = PrincipalKey.ofUser( SYSTEM, "User1" );
            final CreateUserParams createUser1 = CreateUserParams.create()
                .userKey( userKey1 )
                .displayName( "User 1" )
                .email( "user1@enonic.com" )
                .login( "User1" )
                .password( "fisk" )
                .build();

            final User user = securityService.createUser( createUser1 );

            final UsernamePasswordAuthToken authToken = new UsernamePasswordAuthToken();
            authToken.setUsername( "user1" );
            authToken.setPassword( "runar" );
            authToken.setIdProvider( SYSTEM );

            AuthenticationInfo authInfo = securityService.authenticate( authToken );
            assertFalse( authInfo.isAuthenticated() );

            securityService.setPassword( user.getKey(), "runar" );

            AuthenticationInfo authInfo2 = securityService.authenticate( authToken );
            assertTrue( authInfo2.isAuthenticated() );
        } );
    }

    @Test
    void testClearPassword()
    {
        runAsAdmin( () -> {
            final CreateUserParams params = CreateUserParams.create()
                .userKey( PrincipalKey.ofUser( SYSTEM, "User1" ) )
                .displayName( "User 1" )
                .email( "user1@enonic.com" )
                .login( "User1" )
                .password( "fisk" )
                .build();

            final User user = securityService.createUser( params );

            assertNotNull( user.getAuthenticationHash() );

            securityService.setPassword( user.getKey(), null );

            final User updatedUser = securityService.getUser( user.getKey() ).orElse( null );
            assertNotNull( updatedUser );
            assertNull( updatedUser.getAuthenticationHash() );
        } );
    }

    @Test
    public void testQuery()
        throws Exception
    {

        runAsAdmin( () -> {
            final PrincipalKey userKey1 = PrincipalKey.ofUser( SYSTEM, "User1" );

            final CreateUserParams createUser1 = CreateUserParams.create()
                .userKey( userKey1 )
                .displayName( "User 1" )
                .email( "user1@enonic.com" )
                .login( "User1" )
                .password( "123456" )
                .build();

            final PrincipalQuery query = PrincipalQuery.create().idProvider( IdProviderKey.system() ).build();
            PrincipalQueryResult queryResult = securityService.query( query );

            queryResult = securityService.query( query );
            assertEquals( 2, queryResult.getTotalSize() );

            final User user1 = securityService.createUser( createUser1 );

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

        return ContextBuilder.create()
            .authInfo( authInfo )
            .repositoryId( SystemConstants.SYSTEM_REPO_ID )
            .branch( SecurityConstants.BRANCH_SECURITY )
            .build();
    }

    private static class CustomAuthenticationToken
        extends AuthenticationToken
    {
    }

}
