package com.enonic.xp.core.security;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.audit.AuditLogService;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.impl.audit.config.AuditLogConfig;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.core.impl.security.PasswordSecurityService;
import com.enonic.xp.core.impl.security.SecurityAuditLogSupportImpl;
import com.enonic.xp.core.impl.security.SecurityConfig;
import com.enonic.xp.core.impl.security.SecurityInitializer;
import com.enonic.xp.core.impl.security.SecurityServiceImpl;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.impl.shared.LocalSharedMapService;
import com.enonic.xp.internal.blobstore.MemoryBlobStore;
import com.enonic.xp.itest.AbstractElasticsearchIntegrationTest;
import com.enonic.xp.repo.impl.binary.BinaryServiceImpl;
import com.enonic.xp.repo.impl.branch.storage.BranchServiceImpl;
import com.enonic.xp.repo.impl.commit.CommitServiceImpl;
import com.enonic.xp.repo.impl.config.RepoConfiguration;
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
import com.enonic.xp.security.IdProviderConfig;
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
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.security.auth.EmailPasswordAuthToken;
import com.enonic.xp.security.auth.UsernamePasswordAuthToken;
import com.enonic.xp.security.auth.VerifiedEmailAuthToken;
import com.enonic.xp.security.auth.VerifiedUserKeyAuthToken;
import com.enonic.xp.security.auth.VerifiedUsernameAuthToken;

import static com.enonic.xp.security.acl.IdProviderAccess.ADMINISTRATOR;
import static com.enonic.xp.security.acl.IdProviderAccess.CREATE_USERS;
import static com.enonic.xp.security.acl.IdProviderAccess.WRITE_USERS;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

class SecurityServiceImplTest
    extends AbstractElasticsearchIntegrationTest
{
    private static final IdProviderKey SYSTEM = IdProviderKey.system();

    private static final IdProviderAccessControlList USER_ADMIN_ACL = IdProviderAccessControlList.of(
        IdProviderAccessControlEntry.create().principal( RoleKeys.USER_MANAGER_ADMIN ).access( ADMINISTRATOR ).build() );

    protected EventPublisher eventPublisher;

    private SecurityServiceImpl securityService;

    private NodeServiceImpl nodeService;

    @BeforeEach
    void setUp()
    {
        deleteAllIndices();
        final MemoryBlobStore blobStore = new MemoryBlobStore();

        final BinaryServiceImpl binaryService = new BinaryServiceImpl( blobStore );

        final StorageDaoImpl storageDao = new StorageDaoImpl( client );

        final SearchDaoImpl searchDao = new SearchDaoImpl( client );

        final BranchServiceImpl branchService = new BranchServiceImpl( storageDao, searchDao );

        final VersionServiceImpl versionService = new VersionServiceImpl( storageDao );

        final CommitServiceImpl commitService = new CommitServiceImpl( storageDao );

        final NodeVersionServiceImpl nodeDao = new NodeVersionServiceImpl( blobStore, new RepoConfiguration( Map.of() ) );

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
            new RepositoryServiceImpl( repositoryEntryService, nodeRepositoryService, storageService, searchService, branchService,
                                       () -> null );
        SystemRepoInitializer.create()
            .setIndexServiceInternal( indexServiceInternal )
            .setNodeStorageService( storageService )
            .setRepositoryEntryService( repositoryEntryService )
            .setNodeRepositoryService( nodeRepositoryService )
            .build()
            .initialize();

        this.nodeService = new NodeServiceImpl( indexServiceInternal, storageService, searchService, eventPublisher, binaryService );

        IndexServiceImpl indexService =
            new IndexServiceImpl( indexServiceInternal, indexedDataService, searchService, nodeDao, repositoryEntryService );

        AuditLogConfig auditLogConfig = mock( AuditLogConfig.class );
        Mockito.when( auditLogConfig.isEnabled() ).thenReturn( true );
        Mockito.when( auditLogConfig.isOutputLogs() ).thenReturn( true );

        AuditLogService auditLogService = mock( AuditLogService.class, withSettings().stubOnly() );

        SecurityConfig securityConfig = mock( SecurityConfig.class, withSettings().stubOnly()
            .defaultAnswer( invocationOnMock -> invocationOnMock.getMethod().getDefaultValue() ) );

        SecurityAuditLogSupportImpl securityAuditLogSupport = new SecurityAuditLogSupportImpl( auditLogService );
        securityAuditLogSupport.activate( securityConfig );

        final PasswordSecurityService passwordSecurityService = new PasswordSecurityService();
        passwordSecurityService.activate( securityConfig );

        securityService = new SecurityServiceImpl( this.nodeService, securityAuditLogSupport, passwordSecurityService, new LocalSharedMapService() );

        runAsAdmin( () -> SecurityInitializer.create()
            .setIndexService( indexService )
            .setSecurityService( securityService )
            .setNodeService( nodeService )
            .build()
            .initialize() );

    }

    @Test
    void testCreateUser()
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
    void testCreateUserThrowsExceptionWhenNameIsOccupied()
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
    void testCreateUserDuplicatedEmail()
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
    void testUpdateUser()
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
    void testUpdateUserDuplicatedEmail()
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
    void testUpdateUserDuplicatedEmailWithEditor()
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
    void testCreateGroup()
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
    void testCreateGroupThrowsExceptionWhenNameIsOccupied()
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
    void testUpdateGroup()
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
    void testCreateRole()
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
    void testCreateRoleThrowsExceptionWhenNameIsOccupied()
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
    void testUpdateRole()
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
    void testDeleteNonExistPrincipal()
    {
        runAsAdmin( () -> {
            assertThrows( PrincipalNotFoundException.class,
                          () -> securityService.deletePrincipal( PrincipalKey.from( "user:system:invalid" ) ) );
        } );
    }

    @Test
    void testDeletePrincipalWithoutPermissions()
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
    void testAddRelationship()
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
    void testRemoveRelationship()
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
    void testRemoveAllRelationships()
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
    void testRemoveSuFromAdmin()
    {
        runAsAdmin( () -> {
            assertThrows( IllegalArgumentException.class, () -> securityService.removeRelationship(
                PrincipalRelationship.from( RoleKeys.ADMIN ).to( PrincipalKey.ofSuperUser() ) ) );
        } );
    }

    @Test
    void testDeleteAdminRole()
    {
        runAsAdmin( () -> {
            assertThrows( IllegalArgumentException.class, () -> securityService.deletePrincipal( RoleKeys.ADMIN ) );
        } );
    }

    @Test
    void testDeleteSu()
    {
        runAsAdmin( () -> {
            assertThrows( IllegalArgumentException.class, () -> securityService.deletePrincipal( PrincipalKey.ofSuperUser() ) );
        } );
    }


    @Test
    void testAuthenticateByEmailPwd()
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

            final EmailPasswordAuthToken authToken = new EmailPasswordAuthToken( SYSTEM, "user1@enonic.com", "password" );
            final AuthenticationInfo authInfo = securityService.authenticate( authToken );
            assertTrue( authInfo.isAuthenticated() );
            assertEquals( user.getKey(), authInfo.getUser().getKey() );
        } );
    }

    @Test
    void testAuthenticateByEmailPwdWrongPwd()
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

            final EmailPasswordAuthToken authToken = new EmailPasswordAuthToken( SYSTEM, "user1@enonic.com", "password" );
            final AuthenticationInfo authInfo = securityService.authenticate( authToken );
            assertFalse( authInfo.isAuthenticated() );
        } );
    }

    @Test
    void testAuthenticateByUsernamePwd()
    {
        runAsAdmin( () -> {
            final CreateUserParams createUser = CreateUserParams.create()
                .userKey( PrincipalKey.ofUser( SYSTEM, "user1" ) )
                .displayName( "User 1" )
                .email( "user1@enonic.com" )
                .login( "user1" )
                .password( "runar" )
                .build();

            final User user = securityService.createUser( createUser );

            final UsernamePasswordAuthToken authToken = new UsernamePasswordAuthToken( SYSTEM, "User1", "runar" );

            final AuthenticationInfo authInfo = securityService.authenticate( authToken );
            assertTrue( authInfo.isAuthenticated() );
            assertEquals( user.getKey(), authInfo.getUser().getKey() );
        } );
    }

    @Test
    void testAuthenticateByUserEmptyPwd()
    {
        runAsAdmin( () -> {
            final CreateUserParams createUser = CreateUserParams.create()
                .userKey( PrincipalKey.ofUser( SYSTEM, "user1" ) )
                .displayName( "User 1" )
                .email( "user1@enonic.com" )
                .login( "user1" )
                .password( "runar" )
                .build();

            securityService.createUser( createUser );

            final UsernamePasswordAuthToken authToken = new UsernamePasswordAuthToken( SYSTEM, "User1", "" );

            final AuthenticationInfo authInfo = securityService.authenticate( authToken );
            assertFalse( authInfo.isAuthenticated() );
        } );
    }

    @Test
    void testAuthenticateByEmail()
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

            final VerifiedEmailAuthToken authToken = new VerifiedEmailAuthToken( SYSTEM, "user1@enonic.com" );
            final AuthenticationInfo authInfo = securityService.authenticate( authToken );
            assertTrue( authInfo.isAuthenticated() );
            assertEquals( user.getKey(), authInfo.getUser().getKey() );
        } );
    }

    @Test
    void testAuthenticateByUsername()
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

            final VerifiedUsernameAuthToken authToken = new VerifiedUsernameAuthToken( SYSTEM, "user1" );

            final AuthenticationInfo authInfo = securityService.authenticate( authToken );
            assertTrue( authInfo.isAuthenticated() );
            assertEquals( user.getKey(), authInfo.getUser().getKey() );
        } );
    }

    @Test
    void testDisabledUserIsPersisted()
    {
        runAsAdmin( () -> {
            final PrincipalKey userKey = PrincipalKey.ofUser( SYSTEM, "User1" );
            securityService.createUser( CreateUserParams.create()
                                            .userKey( userKey )
                                            .displayName( "User 1" )
                                            .login( "User1" )
                                            .disabled( true )
                                            .build() );
            assertTrue( securityService.getUser( userKey ).orElseThrow().isDisabled() );

            securityService.updateUser( UpdateUserParams.create().userKey( userKey ).editor( edit -> edit.loginDisabled = false ).build() );
            assertFalse( securityService.getUser( userKey ).orElseThrow().isDisabled() );

            securityService.updateUser( UpdateUserParams.create().userKey( userKey ).disabled( true ).build() );
            assertTrue( securityService.getUser( userKey ).orElseThrow().isDisabled() );

            securityService.updateUser( UpdateUserParams.create().userKey( userKey ).displayName( "Renamed" ).build() );
            assertTrue( securityService.getUser( userKey ).orElseThrow().isDisabled() );
        } );
    }

    @Test
    void testSystemUsersCannotBeModified()
    {
        runAsAdmin( () -> {
            final List<PrincipalKey> userKeys =
                List.of( PrincipalKey.ofSuperUser(), PrincipalKey.ofAnonymous(), PrincipalKey.from( "user:system:SU" ),
                         PrincipalKey.from( "user:System:Anonymous" ) );
            for ( PrincipalKey userKey : userKeys )
            {
                final UpdateUserParams disable = UpdateUserParams.create().userKey( userKey ).disabled( true ).build();
                assertThatThrownBy( () -> securityService.updateUser( disable ) ).isInstanceOf( IllegalArgumentException.class )
                    .hasMessageContaining( "cannot be modified" );

                final UpdateUserParams rename =
                    UpdateUserParams.create().userKey( userKey ).editor( edit -> edit.displayName = "Renamed" ).build();
                assertThatThrownBy( () -> securityService.updateUser( rename ) ).isInstanceOf( IllegalArgumentException.class );

                assertThatThrownBy( () -> securityService.setPassword( userKey, "password" ) ).isInstanceOf(
                    IllegalArgumentException.class ).hasMessageContaining( "cannot be modified" );
                assertThatThrownBy( () -> securityService.setPassword( userKey, null ) ).isInstanceOf( IllegalArgumentException.class );
            }

            for ( PrincipalKey userKey : List.of( PrincipalKey.ofSuperUser(), PrincipalKey.ofAnonymous() ) )
            {
                final User user = securityService.getUser( userKey ).orElseThrow();
                assertNull( user.getAuthenticationHash() );
                assertFalse( user.isDisabled() );
                assertNotEquals( "Renamed", user.getDisplayName() );
            }
        } );
    }

    @Test
    void testSystemUserProfileCanBeModified()
    {
        runAsAdmin( () -> {
            final PrincipalKey su = PrincipalKey.ofSuperUser();
            final String displayName = securityService.getUser( su ).orElseThrow().getDisplayName();

            final User updated = securityService.updateUser(
                UpdateUserParams.create().userKey( su ).editor( edit -> edit.profile.setString( "dismissed", "banner" ) ).build() );

            assertEquals( "banner", updated.getProfile().getString( "dismissed" ) );
            assertEquals( "banner", securityService.getUser( su ).orElseThrow().getProfile().getString( "dismissed" ) );

            final UpdateUserParams profileAndName = UpdateUserParams.create().userKey( su ).editor( edit -> {
                edit.profile.setString( "dismissed", "again" );
                edit.displayName = "Renamed";
            } ).build();
            assertThatThrownBy( () -> securityService.updateUser( profileAndName ) ).isInstanceOf( IllegalArgumentException.class )
                .hasMessageContaining( "cannot be modified" );

            final User unchanged = securityService.getUser( su ).orElseThrow();
            assertEquals( displayName, unchanged.getDisplayName() );
            assertEquals( "banner", unchanged.getProfile().getString( "dismissed" ) );
        } );
    }

    @Test
    void testAuthenticateDisabledUser()
    {
        runAsAdmin( () -> {
            final PrincipalKey userKey = PrincipalKey.ofUser( SYSTEM, "User1" );
            securityService.createUser( CreateUserParams.create()
                                            .userKey( userKey )
                                            .displayName( "User 1" )
                                            .email( "user1@enonic.com" )
                                            .login( "User1" )
                                            .password( "password" )
                                            .build() );

            assertTrue( securityService.authenticate( new UsernamePasswordAuthToken( SYSTEM, "user1", "password" ) ).isAuthenticated() );

            securityService.updateUser( UpdateUserParams.create().userKey( userKey ).editor( edit -> edit.loginDisabled = true ).build() );

            assertFalse( securityService.authenticate( new UsernamePasswordAuthToken( SYSTEM, "user1", "password" ) ).isAuthenticated() );
            assertFalse( securityService.authenticate( new EmailPasswordAuthToken( SYSTEM, "user1@enonic.com", "password" ) ).isAuthenticated() );
            assertFalse( securityService.authenticate( new VerifiedUsernameAuthToken( SYSTEM, "user1" ) ).isAuthenticated() );
            assertFalse( securityService.authenticate( new VerifiedEmailAuthToken( SYSTEM, "user1@enonic.com" ) ).isAuthenticated() );
            assertFalse( securityService.authenticate( new VerifiedUserKeyAuthToken( userKey ) ).isAuthenticated() );

            securityService.updateUser( UpdateUserParams.create().userKey( userKey ).editor( edit -> edit.loginDisabled = false ).build() );

            assertTrue( securityService.authenticate( new UsernamePasswordAuthToken( SYSTEM, "user1", "password" ) ).isAuthenticated() );
            assertTrue( securityService.authenticate( new VerifiedUserKeyAuthToken( userKey ) ).isAuthenticated() );
        } );
    }

    @Test
    void testGetUserMemberships()
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
    void testGetAllMemberships()
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
    void testCreateIdProvider()
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

            securityService.createIdProvider( createIdProvider );

            securityService.deleteIdProvider( IdProviderKey.from( "enonic" ) );

            assertNull( securityService.getIdProvider( IdProviderKey.from( "enonic" ) ) );
        } );
    }

    @Test
    void testCreateIdProviderThrowsExceptionWhenNameIsOccupied()
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
    void testUpdateIdProvider()
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
    void testUpdateIdProviderWithEditor()
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
    void setPassword()
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

            final UsernamePasswordAuthToken authToken = new UsernamePasswordAuthToken( SYSTEM, "user1", "runar" );

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
    void testQuery()
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

    @Test
    void testOnlyAdministratorChangesAdministratorMembership()
    {
        final PrincipalKey user = createUser( "user1" );
        final PrincipalKey admins = runAsAdmin( () -> {
            final PrincipalKey group = PrincipalKey.ofGroup( SYSTEM, "admins" );
            securityService.createGroup( CreateGroupParams.create().groupKey( group ).displayName( "Admins" ).build() );
            securityService.addRelationship( PrincipalRelationship.from( RoleKeys.ADMIN ).to( group ) );
            return group;
        } );

        runAsUserAdmin( () -> {
            assertThatThrownBy( () -> securityService.addRelationship( PrincipalRelationship.from( RoleKeys.ADMIN ).to( user ) ) )
                .isInstanceOf( IllegalArgumentException.class )
                .hasMessageContaining( "only be changed by an administrator" );
            assertThatThrownBy( () -> securityService.addRelationship( PrincipalRelationship.from( admins ).to( user ) ) ).isInstanceOf(
                IllegalArgumentException.class );
            assertThatThrownBy( () -> securityService.removeRelationship( PrincipalRelationship.from( RoleKeys.ADMIN ).to( admins ) ) )
                .isInstanceOf( IllegalArgumentException.class );

            // other roles stay delegated
            securityService.addRelationship( PrincipalRelationship.from( RoleKeys.USER_MANAGER_ADMIN ).to( user ) );
        } );

        runAsAdmin( () -> {
            assertFalse( securityService.getAllMemberships( user ).contains( RoleKeys.ADMIN ) );
            assertTrue( securityService.getMemberships( user ).contains( RoleKeys.USER_MANAGER_ADMIN ) );

            securityService.addRelationship( PrincipalRelationship.from( admins ).to( user ) );
            assertTrue( securityService.getAllMemberships( user ).contains( RoleKeys.ADMIN ) );
            securityService.removeRelationship( PrincipalRelationship.from( admins ).to( user ) );
            assertFalse( securityService.getAllMemberships( user ).contains( RoleKeys.ADMIN ) );
        } );
    }

    @Test
    void testOnlyAdministratorModifiesAdministrator()
    {
        final PrincipalKey admin = createUser( "admin1" );
        final PrincipalKey user = createUser( "user1" );
        runAsAdmin( () -> {
            final PrincipalKey group = PrincipalKey.ofGroup( SYSTEM, "admins" );
            securityService.createGroup( CreateGroupParams.create().groupKey( group ).displayName( "Admins" ).build() );
            securityService.addRelationship( PrincipalRelationship.from( RoleKeys.ADMIN ).to( group ) );
            securityService.addRelationship( PrincipalRelationship.from( group ).to( admin ) );
        } );

        runAsUserAdmin( () -> {
            assertThatThrownBy( () -> securityService.setPassword( admin, "taken-over" ) ).isInstanceOf( IllegalArgumentException.class )
                .hasMessageContaining( "only be modified by an administrator" );
            assertThatThrownBy( () -> securityService.setPassword( admin, null ) ).isInstanceOf( IllegalArgumentException.class );
            assertThatThrownBy(
                () -> securityService.updateUser( UpdateUserParams.create().userKey( admin ).email( "attacker@example.com" ).build() ) )
                .isInstanceOf( IllegalArgumentException.class );
            assertThatThrownBy( () -> securityService.updateUser(
                UpdateUserParams.create().userKey( admin ).editor( edit -> edit.profile.setString( "publicKey", "planted" ) ).build() ) )
                .isInstanceOf( IllegalArgumentException.class );
            assertThatThrownBy( () -> securityService.deletePrincipal( admin ) ).isInstanceOf( IllegalArgumentException.class );
            assertThatThrownBy( () -> securityService.deletePrincipal( PrincipalKey.ofGroup( SYSTEM, "admins" ) ) ).isInstanceOf(
                IllegalArgumentException.class );

            // everyone else stays manageable
            securityService.setPassword( user, "password" );
            securityService.updateUser( UpdateUserParams.create().userKey( user ).email( "user1@example.com" ).build() );
            securityService.deletePrincipal( user );
        } );

        runAsAdmin( () -> {
            final User unchanged = securityService.getUser( admin ).orElseThrow();
            assertNull( unchanged.getAuthenticationHash() );
            assertEquals( "admin1@enonic.com", unchanged.getEmail() );
            assertNull( unchanged.getProfile().getString( "publicKey" ) );

            securityService.setPassword( admin, "password" );
            securityService.updateUser(
                UpdateUserParams.create().userKey( admin ).editor( edit -> edit.profile.setString( "publicKey", "own" ) ).build() );
            assertEquals( "own", securityService.getUser( admin ).orElseThrow().getProfile().getString( "publicKey" ) );
        } );
    }

    @Test
    void testPlatformPrincipalsCannotBeRemoved()
    {
        final List<PrincipalKey> platformPrincipals =
            List.of( PrincipalKey.ofSuperUser(), PrincipalKey.ofAnonymous(), PrincipalKey.from( "user:system:Anonymous" ), RoleKeys.ADMIN,
                     RoleKeys.EVERYONE, RoleKeys.AUTHENTICATED, RoleKeys.ADMIN_LOGIN, RoleKeys.USER_MANAGER_APP,
                     RoleKeys.USER_MANAGER_ADMIN, RoleKeys.CONTENT_MANAGER_EXPERT, RoleKeys.CONTENT_MANAGER_ADMIN, RoleKeys.AUDIT_LOG,
                     RoleKeys.SCHEMA_ADMIN );
        runAsAdmin( () -> {
            for ( PrincipalKey key : platformPrincipals )
            {
                assertThatThrownBy( () -> securityService.deletePrincipal( key ) ).isInstanceOf( IllegalArgumentException.class )
                    .hasMessageContaining( "cannot be removed" );
                assertTrue( securityService.getPrincipal( key ).isPresent(), key.toString() );
            }
        } );
    }

    @Test
    void testReservedRoleNamespaces()
    {
        final PrincipalKey projectOwner = PrincipalKey.ofRole( "cms.project.payroll.owner" );
        runAsUserAdmin( () -> {
            for ( PrincipalKey key : List.of( projectOwner, PrincipalKey.ofRole( "system.anything" ),
                                              PrincipalKey.ofRole( "CMS.Something" ) ) )
            {
                final CreateRoleParams params = CreateRoleParams.create().roleKey( key ).displayName( "x" ).build();
                assertThatThrownBy( () -> securityService.createRole( params ) )
                    .isInstanceOf( IllegalArgumentException.class )
                    .hasMessageContaining( "reserved namespace" );
            }
            securityService.createRole( CreateRoleParams.create().roleKey( PrincipalKey.ofRole( "editors" ) ).displayName( "x" ).build() );
        } );

        runAsAdmin(
            () -> securityService.createRole( CreateRoleParams.create().roleKey( projectOwner ).displayName( "Owner" ).build() ) );

        runAsUserAdmin( () -> assertThatThrownBy( () -> securityService.deletePrincipal( projectOwner ) ).isInstanceOf(
            IllegalArgumentException.class ).hasMessageContaining( "removed with its project" ) );

        runAsAdmin( () -> {
            securityService.deletePrincipal( projectOwner );
            assertTrue( securityService.getRole( projectOwner ).isEmpty() );
        } );
    }

    @Test
    void testSystemIdProviderCannotBeRemoved()
    {
        runAsAdmin( () -> {
            assertThatThrownBy( () -> securityService.deleteIdProvider( IdProviderKey.system() ) ).isInstanceOf(
                IllegalArgumentException.class ).hasMessageContaining( "cannot be removed" );
            assertNotNull( securityService.getIdProvider( IdProviderKey.system() ) );
            assertTrue( securityService.getUser( PrincipalKey.ofSuperUser() ).isPresent() );
        } );
    }

    @Test
    void testIdProviderHoldingAdministratorsRemovedOnlyByAdministrator()
    {
        final IdProviderKey corp = IdProviderKey.from( "corp" );
        final IdProviderKey partners = IdProviderKey.from( "partners" );
        runAsAdmin( () -> {
            for ( IdProviderKey key : List.of( corp, partners ) )
            {
                securityService.createIdProvider( CreateIdProviderParams.create()
                                                      .key( key )
                                                      .displayName( key.toString() )
                                                      .permissions( USER_ADMIN_ACL )
                                                      .build() );
            }
            final PrincipalKey corpAdmin = PrincipalKey.ofUser( corp, "boss" );
            securityService.createUser( CreateUserParams.create().userKey( corpAdmin ).login( "boss" ).displayName( "Boss" ).build() );
            final PrincipalKey group = PrincipalKey.ofGroup( SYSTEM, "admins" );
            securityService.createGroup( CreateGroupParams.create().groupKey( group ).displayName( "Admins" ).build() );
            securityService.addRelationship( PrincipalRelationship.from( RoleKeys.ADMIN ).to( group ) );
            securityService.addRelationship( PrincipalRelationship.from( group ).to( corpAdmin ) );
        } );

        runAsUserAdmin( () -> {
            assertThatThrownBy( () -> securityService.deleteIdProvider( corp ) ).isInstanceOf( IllegalArgumentException.class )
                .hasMessageContaining( "holds administrators" );
            securityService.deleteIdProvider( partners );
        } );

        runAsAdmin( () -> {
            assertNull( securityService.getIdProvider( partners ) );
            assertNotNull( securityService.getIdProvider( corp ) );
            securityService.deleteIdProvider( corp );
            assertNull( securityService.getIdProvider( corp ) );
        } );
    }

    @Test
    void testSystemIdProviderStaysBound()
    {
        runAsAdmin( () -> {
            assertThatThrownBy( () -> securityService.updateIdProvider(
                UpdateIdProviderParams.create().key( IdProviderKey.system() ).editor( edit -> edit.idProviderConfig = null ).build() ) )
                .isInstanceOf( IllegalArgumentException.class )
                .hasMessageContaining( "cannot be unbound" );
            assertNotNull( securityService.getIdProvider( IdProviderKey.system() ).getIdProviderConfig() );
        } );

        runAsUserAdmin( () -> {
            final IdProviderConfig other =
                IdProviderConfig.create().applicationKey( ApplicationKey.from( "com.example.idp" ) ).config( new PropertyTree() ).build();
            assertThatThrownBy( () -> securityService.updateIdProvider(
                UpdateIdProviderParams.create().key( IdProviderKey.system() ).idProviderConfig( other ).build() ) ).isInstanceOf(
                IllegalArgumentException.class ).hasMessageContaining( "by an administrator" );

            final IdProvider renamed = securityService.updateIdProvider(
                UpdateIdProviderParams.create().key( IdProviderKey.system() ).displayName( "Staff" ).build() );
            assertEquals( "Staff", renamed.getDisplayName() );
            assertNotNull( renamed.getIdProviderConfig() );
        } );
    }

    @Test
    void testIdProviderKeyMustBeUsableForPrincipals()
    {
        runAsAdmin( () -> {
            for ( String key : List.of( "a#b", "corp@eu", "тест", "😀", "a+b", "corp.eu" ) )
            {
                final CreateIdProviderParams params =
                    CreateIdProviderParams.create().key( IdProviderKey.from( key ) ).displayName( key ).permissions( USER_ADMIN_ACL ).build();
                assertThatThrownBy( () -> securityService.createIdProvider( params ) ).as( key )
                    .isInstanceOf( IllegalArgumentException.class )
                    .hasMessageContaining( "Invalid IdProviderKey" );
                assertNull( securityService.getIdProvider( IdProviderKey.from( key ) ) );
            }

            final IdProviderKey valid = IdProviderKey.from( "My-Provider_1" );
            securityService.createIdProvider(
                CreateIdProviderParams.create().key( valid ).displayName( "valid" ).permissions( USER_ADMIN_ACL ).build() );
            securityService.createUser( CreateUserParams.create()
                                            .userKey( PrincipalKey.ofUser( valid, "user" ) )
                                            .login( "user" )
                                            .displayName( "User" )
                                            .build() );
        } );
    }

    private PrincipalKey createUser( final String name )
    {
        return runAsAdmin( () -> securityService.createUser( CreateUserParams.create()
                                                                 .userKey( PrincipalKey.ofUser( SYSTEM, name ) )
                                                                 .login( name )
                                                                 .email( name + "@enonic.com" )
                                                                 .displayName( name )
                                                                 .build() ).getKey() );
    }

    private void runAsUserAdmin( Runnable runnable )
    {
        final AuthenticationInfo authInfo = AuthenticationInfo.create()
            .principals( RoleKeys.USER_MANAGER_ADMIN, RoleKeys.ADMIN_LOGIN, RoleKeys.AUTHENTICATED, RoleKeys.EVERYONE )
            .user( User.create().key( PrincipalKey.ofUser( SYSTEM, "manager" ) ).login( "manager" ).build() )
            .build();
        ContextBuilder.create()
            .authInfo( authInfo )
            .repositoryId( SystemConstants.SYSTEM_REPO_ID )
            .branch( SecurityConstants.BRANCH_SECURITY )
            .build()
            .runWith( runnable );
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
        final AuthenticationInfo authInfo = AuthenticationInfo.create().principals( RoleKeys.ADMIN ).user( User.anonymous() ).build();

        return ContextBuilder.create()
            .authInfo( authInfo )
            .repositoryId( SystemConstants.SYSTEM_REPO_ID )
            .branch( SecurityConstants.BRANCH_SECURITY )
            .build();
    }

}
