package com.enonic.xp.core.project;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.audit.AuditLogService;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.core.impl.project.ProjectAccessException;
import com.enonic.xp.core.impl.project.ProjectAccessHelper;
import com.enonic.xp.core.impl.project.ProjectCircleDependencyException;
import com.enonic.xp.core.impl.project.ProjectConfig;
import com.enonic.xp.core.impl.project.ProjectMultipleParentsException;
import com.enonic.xp.core.impl.project.ProjectPermissionsContextManagerImpl;
import com.enonic.xp.core.impl.project.ProjectServiceImpl;
import com.enonic.xp.core.impl.security.SecurityAuditLogSupportImpl;
import com.enonic.xp.core.impl.security.SecurityConfig;
import com.enonic.xp.core.impl.security.SecurityInitializer;
import com.enonic.xp.core.impl.security.SecurityServiceImpl;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.project.CreateProjectParams;
import com.enonic.xp.project.ModifyProjectIconParams;
import com.enonic.xp.project.ModifyProjectParams;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.project.ProjectGraph;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectNotFoundException;
import com.enonic.xp.project.ProjectPermissions;
import com.enonic.xp.project.ProjectRole;
import com.enonic.xp.project.Projects;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.index.IndexServiceInternal;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.CreateUserParams;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.PrincipalRelationship;
import com.enonic.xp.security.PrincipalRelationships;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.security.User;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProjectServiceImplTest
    extends AbstractNodeTest
{
    private static final User REPO_TEST_DEFAULT_USER =
        User.create().key( PrincipalKey.ofUser( IdProviderKey.system(), "repo-test-user" ) ).login( "repo-test-user" ).build();

    private static final User REPO_TEST_OWNER =
        User.create().key( PrincipalKey.ofUser( IdProviderKey.system(), "custom-user" ) ).login( "custom-user" ).build();

    private static final AuthenticationInfo REPO_TEST_ADMIN_USER_AUTHINFO = AuthenticationInfo.create()
        .principals( RoleKeys.AUTHENTICATED )
        .principals( RoleKeys.ADMIN )
        .user( REPO_TEST_DEFAULT_USER )
        .build();

    private static final AuthenticationInfo REPO_TEST_CONTENT_ADMIN_AUTHINFO = AuthenticationInfo.create()
        .principals( RoleKeys.AUTHENTICATED )
        .principals( RoleKeys.CONTENT_MANAGER_ADMIN )
        .user( REPO_TEST_DEFAULT_USER )
        .build();

    private static final AuthenticationInfo REPO_TEST_CONTENT_MANAGER_AUTHINFO = AuthenticationInfo.create()
        .principals( RoleKeys.AUTHENTICATED )
        .principals( RoleKeys.CONTENT_MANAGER_APP )
        .user( REPO_TEST_DEFAULT_USER )
        .build();

    private static final AuthenticationInfo REPO_TEST_CUSTOM_MANAGER_AUTHINFO = AuthenticationInfo.create()
        .principals( RoleKeys.AUTHENTICATED )
        .principals( RoleKeys.CONTENT_MANAGER_APP )
        .principals( ProjectAccessHelper.createRoleKey( ProjectName.from( "test-project" ), ProjectRole.OWNER ) )
        .user( REPO_TEST_OWNER )
        .build();

    private ProjectServiceImpl projectService;

    private SecurityServiceImpl securityService;

    private ProjectConfig projectConfig;

    ProjectServiceImplTest()
    {
        super( true );
    }

    private static Context adminContext()
    {
        return ContextBuilder.create()
            .branch( "master" )
            .repositoryId( SystemConstants.SYSTEM_REPO_ID )
            .authInfo( REPO_TEST_ADMIN_USER_AUTHINFO )
            .build();
    }

    private static Context contentAdminContext()
    {
        return ContextBuilder.create()
            .branch( "master" )
            .repositoryId( SystemConstants.SYSTEM_REPO_ID )
            .authInfo( REPO_TEST_CONTENT_ADMIN_AUTHINFO )
            .build();
    }

    private static Context contentManagerContext()
    {
        return ContextBuilder.create()
            .branch( "master" )
            .repositoryId( SystemConstants.SYSTEM_REPO_ID )
            .authInfo( REPO_TEST_CONTENT_MANAGER_AUTHINFO )
            .build();
    }

    private static Context contentCustomManagerContext()
    {
        return ContextBuilder.create()
            .branch( "master" )
            .repositoryId( SystemConstants.SYSTEM_REPO_ID )
            .authInfo( REPO_TEST_CUSTOM_MANAGER_AUTHINFO )
            .build();
    }

    private static Context contextWithAuthInfo( final AuthenticationInfo authenticationInfo )
    {
        return ContextBuilder.create()
            .branch( "master" )
            .repositoryId( SystemConstants.SYSTEM_REPO_ID )
            .authInfo( authenticationInfo )
            .build();
    }

    @BeforeEach
    void setUp()
    {
        final SecurityConfig securityConfig = mock( SecurityConfig.class );
        when( securityConfig.auditlog_enabled() ).thenReturn( true );

        projectConfig = mock( ProjectConfig.class );

        final AuditLogService auditLogService = mock( AuditLogService.class );

        final SecurityAuditLogSupportImpl securityAuditLogSupport = new SecurityAuditLogSupportImpl( auditLogService );
        securityAuditLogSupport.activate( securityConfig );

        securityService = new SecurityServiceImpl( this.nodeService, securityAuditLogSupport );

        adminContext().runWith( () -> {
            SecurityInitializer.create()
                .setIndexService( indexService )
                .setSecurityService( securityService )
                .setNodeService( nodeService )
                .build()
                .initialize();

            final ProjectPermissionsContextManagerImpl projectAccessContextManager = new ProjectPermissionsContextManagerImpl();

            projectService =
                new ProjectServiceImpl( repositoryService, indexService, nodeService, securityService, projectAccessContextManager,
                                        eventPublisher, projectConfig );

            projectService.initialize();
        } );
    }

    @Test
    void initialize()
    {
        ContextBuilder.from( adminContext() )
            .repositoryId( ContentConstants.CONTENT_REPO_ID )
            .branch( ContentConstants.BRANCH_DRAFT )
            .build()
            .runWith( () -> {
                final Projects projects = projectService.list();

                assertEquals( 1, projects.getSize() );
                assertTrue( nodeService.nodeExists( new NodePath( "/content" ) ) );
                assertTrue( nodeService.nodeExists( new NodePath( "/issues" ) ) );
                assertTrue( nodeService.nodeExists( new NodePath( "/archive" ) ) );
            } );
    }

    @Test
    void create()
    {
        final RepositoryId projectRepoId = RepositoryId.from( "com.enonic.cms.test-project" );

        final Project project = doCreateProjectAsAdmin( ProjectName.from( projectRepoId ) );
        assertNotNull( project );
        assertEquals( "test-project", project.getName().toString() );

        final NodeBranchEntry nodeBranchEntry =
            this.branchService.get( Node.ROOT_UUID, InternalContext.create( adminContext() ).repositoryId( projectRepoId ).build() );
        assertNotNull( nodeBranchEntry );

        adminContext().runWith( () -> {
            final Repository pro = repositoryService.get( projectRepoId );
            assertNotNull( pro );
        } );
    }

    @Test
    void create_with_data()
    {
        final RepositoryId projectRepoId = RepositoryId.from( "com.enonic.cms.test-project" );
        final String displayName = "test display name";
        final String description = "test description";

        final PropertyTree data = new PropertyTree();

        final PropertySet projectData = data.addSet( ProjectConstants.PROJECT_DATA_SET_NAME );

        projectData.setString( ProjectConstants.PROJECT_DISPLAY_NAME_PROPERTY, displayName );
        projectData.setString( ProjectConstants.PROJECT_DESCRIPTION_PROPERTY, description );

        adminContext().callWith( () -> doCreateProject( ProjectName.from( projectRepoId ), displayName, description ) );

        adminContext().runWith( () -> {
            final Repository projectRepo = repositoryService.get( projectRepoId );
            assertEquals( data, projectRepo.getData() );
        } );
    }

    @Test
    void create_in_non_master_node()
    {
        IndexServiceInternal indexServiceInternalMock = mock( IndexServiceInternal.class );
        when( indexServiceInternalMock.waitForYellowStatus() ).thenReturn( true );

        indexService.setIndexServiceInternal( indexServiceInternalMock );

        final RepositoryId projectRepoId = RepositoryId.from( "com.enonic.cms.test-project" );

        final Project project = adminContext().callWith( () -> doCreateProject( ProjectName.from( projectRepoId ), null, true, null ) );
        assertNotNull( project );
        assertEquals( "test-project", project.getName().toString() );

        final NodeBranchEntry nodeBranchEntry =
            this.branchService.get( Node.ROOT_UUID, InternalContext.create( adminContext() ).repositoryId( projectRepoId ).build() );
        assertNotNull( nodeBranchEntry );

        adminContext().runWith( () -> {
            final Repository pro = repositoryService.get( projectRepoId );
            assertNotNull( pro );
        } );
    }

    @Test
    void create_with_content_admin_permissions()
    {
        final RepositoryId projectRepoId = RepositoryId.from( "com.enonic.cms.test-project" );

        final Project project = contentAdminContext().callWith( () -> doCreateProject( ProjectName.from( projectRepoId ) ) );

        assertNotNull( project );
    }

    @Test
    void create_without_permissions()
    {
        final RepositoryId projectRepoId = RepositoryId.from( "com.enonic.cms.test-project" );

        final RuntimeException ex =
            Assertions.assertThrows( RuntimeException.class, () -> doCreateProject( ProjectName.from( projectRepoId ) ) );

        assertEquals( "Denied [user:system:test-user] user access for [create] operation", ex.getMessage() );
    }

    @Test
    void create_with_custom_permissions()
    {
        final RepositoryId projectRepoId = RepositoryId.from( "com.enonic.cms.test-project" );

        contentCustomManagerContext().runWith( () -> {
            final RuntimeException ex =
                Assertions.assertThrows( RuntimeException.class, () -> doCreateProject( ProjectName.from( projectRepoId ) ) );

            assertEquals( "Denied [user:system:custom-user] user access for [create] operation", ex.getMessage() );
        } );

    }

    @Test
    void create_with_roles()
    {
        final RepositoryId projectRepoId = RepositoryId.from( "com.enonic.cms.test-project" );

        doCreateProjectAsAdmin( ProjectName.from( projectRepoId ) );

        assertTrue( securityService.getRole( PrincipalKey.ofRole( "cms.project.test-project.owner" ) ).isPresent() );
        assertTrue( securityService.getRole( PrincipalKey.ofRole( "cms.project.test-project.author" ) ).isPresent() );
        assertTrue( securityService.getRole( PrincipalKey.ofRole( "cms.project.test-project.contributor" ) ).isPresent() );
        assertTrue( securityService.getRole( PrincipalKey.ofRole( "cms.project.test-project.editor" ) ).isPresent() );
        assertTrue( securityService.getRole( PrincipalKey.ofRole( "cms.project.test-project.viewer" ) ).isPresent() );
    }

    @Test
    void create_with_role_members()
    {
        final RepositoryId projectRepoId = RepositoryId.from( "com.enonic.cms.test-project" );

        adminContext().runWith( () -> {
            final User user1 = securityService.createUser( CreateUserParams.create()
                                                               .userKey( PrincipalKey.ofUser( IdProviderKey.system(), "user1" ) )
                                                               .displayName( "user1" )
                                                               .login( "user1" )
                                                               .build() );

            final User user2 = securityService.createUser( CreateUserParams.create()
                                                               .userKey( PrincipalKey.ofUser( IdProviderKey.system(), "user2" ) )
                                                               .displayName( "user2" )
                                                               .login( "user2" )
                                                               .build() );

            doCreateProjectAsAdmin( ProjectName.from( projectRepoId ),
                                    ProjectPermissions.create().addOwner( user1.getKey() ).addOwner( user2.getKey() ).build() );

            final Set<PrincipalKey> members = securityService.getRelationships( PrincipalKey.ofRole( "cms.project.test-project.owner" ) )
                .stream()
                .map( PrincipalRelationship::getTo )
                .collect( Collectors.toSet() );

            assertTrue( members.contains( user1.getKey() ) );
            assertTrue( members.contains( user2.getKey() ) );

        } );
    }

    @Test
    void create_with_root_content_permissions()
    {
        final RepositoryId projectRepoId = RepositoryId.from( "com.enonic.cms.test-project" );
        final ProjectName projectName = ProjectName.from( projectRepoId );

        doCreateProjectAsAdmin( projectName );

        List.of( ContextBuilder.from( adminContext() ).branch( ContentConstants.BRANCH_DRAFT ).repositoryId( projectRepoId ).build(),
                 ContextBuilder.from( adminContext() ).branch( ContentConstants.BRANCH_MASTER ).repositoryId( projectRepoId ).build() )
            .forEach( context -> context.runWith( () -> {

                final Node rootContentNode = nodeService.getByPath( ContentConstants.CONTENT_ROOT_PATH );
                final AccessControlList rootContentPermissions = rootContentNode.getPermissions();

                assertTrue( rootContentPermissions.getEntry( RoleKeys.ADMIN ).isAllowedAll() );
                assertTrue( rootContentPermissions.getEntry( RoleKeys.CONTENT_MANAGER_ADMIN ).isAllowedAll() );
                assertTrue( rootContentPermissions.getEntry( PrincipalKey.ofRole( "cms.project.test-project.owner" ) ).isAllowedAll() );
                assertTrue( rootContentPermissions.getEntry( PrincipalKey.ofRole( "cms.project.test-project.editor" ) ).isAllowedAll() );
                assertTrue( rootContentPermissions.getEntry( PrincipalKey.ofRole( "cms.project.test-project.author" ) )
                                .isAllowed( Permission.READ, Permission.CREATE, Permission.MODIFY, Permission.DELETE ) );
                assertTrue( rootContentPermissions.getEntry( PrincipalKey.ofRole( "cms.project.test-project.contributor" ) )
                                .isAllowed( Permission.READ ) );
                assertTrue( rootContentPermissions.getEntry( PrincipalKey.ofRole( "cms.project.test-project.viewer" ) )
                                .isAllowed( Permission.READ ) );
                assertNull( rootContentPermissions.getEntry( RoleKeys.EVERYONE ) );
            } ) );
    }

    @Test
    void create_with_root_issues_permissions()
    {
        final RepositoryId projectRepoId = RepositoryId.from( "com.enonic.cms.test-project" );
        final ProjectName projectName = ProjectName.from( projectRepoId );

        doCreateProjectAsAdmin( projectName );

        ContextBuilder.from( adminContext() ).branch( ContentConstants.BRANCH_DRAFT ).repositoryId( projectRepoId ).build().runWith( () -> {

            final Node rootIssuesNode = nodeService.getByPath( new NodePath( "/issues" ) );
            final AccessControlList rootContentPermissions = rootIssuesNode.getPermissions();

            assertAll( () -> assertTrue( rootContentPermissions.getEntry( RoleKeys.ADMIN ).isAllowedAll() ),
                       () -> assertTrue( rootContentPermissions.getEntry( RoleKeys.CONTENT_MANAGER_ADMIN ).isAllowedAll() ),
                       () -> assertTrue( rootContentPermissions.isAllowedFor( PrincipalKey.ofRole( "cms.project.test-project.viewer" ),
                                                                              Permission.READ ) ) );

            PrincipalKeys.from( PrincipalKey.ofRole( "cms.project.test-project.owner" ),
                                PrincipalKey.ofRole( "cms.project.test-project.editor" ),
                                PrincipalKey.ofRole( "cms.project.test-project.contributor" ),
                                PrincipalKey.ofRole( "cms.project.test-project.author" ) )
                .forEach( principalKey -> assertTrue(
                    rootContentPermissions.isAllowedFor( principalKey, Permission.READ, Permission.CREATE, Permission.MODIFY,
                                                         Permission.DELETE ) ) );
        } );
    }

    @Test
    void create_project_with_public_readAccess()
    {
        final RepositoryId projectRepoId = RepositoryId.from( "com.enonic.cms.test-project" );
        final ProjectName projectName = ProjectName.from( projectRepoId );

        adminContext().callWith( () -> doCreateProject( projectName, null, false, null, AccessControlList.create()
            .add( AccessControlEntry.create().principal( RoleKeys.EVERYONE ).allow( Permission.READ ).build() )
            .build(), null ) );

        ContextBuilder.from( adminContext() ).branch( ContentConstants.BRANCH_DRAFT ).repositoryId( projectRepoId ).build().runWith( () -> {

            final Node rootContentNode = nodeService.getByPath( ContentConstants.CONTENT_ROOT_PATH );
            final AccessControlList rootContentPermissions = rootContentNode.getPermissions();

            assertTrue( rootContentPermissions.getEntry( RoleKeys.EVERYONE ).isAllowed( Permission.READ ) );
        } );
    }

    @Test
    void create_with_applications()
    {
        final RepositoryId projectRepoId = RepositoryId.from( "com.enonic.cms.test-project" );

        final PropertyTree config = new PropertyTree();
        config.addString( "a", "value" );
        config.addBoolean( "b", true );
        config.addSet( "c", config.newSet() );

        config.getSet( "c" ).addInstant( "d", Instant.parse( "2022-12-03T10:15:30.00Z" ) );

        final SiteConfigs siteConfigs = SiteConfigs.create()
            .add( SiteConfig.create().application( ApplicationKey.from( "app1" ) ).config( config ).build() )
            .add( SiteConfig.create().application( ApplicationKey.from( "app2" ) ).config( new PropertyTree() ).build() )
            .build();

        final Project project = adminContext().callWith( () -> doCreateProject( ProjectName.from( projectRepoId ), siteConfigs ) );

        assertEquals( 2, project.getSiteConfigs().getSize() );
        assertEquals( ApplicationKey.from( "app1" ), project.getSiteConfigs().get( 0 ).getApplicationKey() );
        assertEquals( ApplicationKey.from( "app2" ), project.getSiteConfigs().get( 1 ).getApplicationKey() );
        assertEquals( config, project.getSiteConfigs().get( 0 ).getConfig() );
        assertEquals( new PropertyTree(), project.getSiteConfigs().get( 1 ).getConfig() );
    }

    @Test
    void delete()
    {
        final ProjectName projectName = ProjectName.from( "test-project" );
        doCreateProjectAsAdmin( projectName );

        adminContext().runWith( () -> {

            assertNotNull( this.projectService.get( projectName ) );

            this.projectService.delete( projectName );

            assertNull( this.repositoryService.get( projectName.getRepoId() ) );

        } );
    }

    @Test
    void delete_with_content_admin_permissions()
    {
        final ProjectName projectName = ProjectName.from( "test-project" );
        doCreateProjectAsAdmin( projectName );

        contentAdminContext().runWith( () -> {
            assertNotNull( this.projectService.get( projectName ) );

            this.projectService.delete( projectName );

            assertNull( this.projectService.get( projectName ) );
        } );
    }

    @Test
    void delete_with_custom_permissions()
    {
        final ProjectName projectName = ProjectName.from( "test-project" );
        doCreateProjectAsAdmin( projectName );
        adminContext().runWith( () -> assertNotNull( this.projectService.get( projectName ) ) );

        contentCustomManagerContext().runWith( () -> {

            final RuntimeException ex = Assertions.assertThrows( RuntimeException.class, () -> projectService.delete( projectName ) );

            assertEquals( "Denied [user:system:custom-user] user access for [delete] operation", ex.getMessage() );
        } );
    }

    @Test
    void delete_without_permissions()
    {
        final ProjectName projectName = ProjectName.from( "test-project" );
        doCreateProjectAsAdmin( projectName );

        adminContext().runWith( () -> {
            assertNotNull( this.projectService.get( projectName ) );
        } );

        final RuntimeException ex = Assertions.assertThrows( RuntimeException.class, () -> projectService.delete( projectName ) );

        assertEquals( "Denied [user:system:test-user] user access for [delete] operation", ex.getMessage() );
    }

    @Test
    void delete_default_project_as_admin()
    {
        adminContext().runWith( () -> {
            assertNotNull( this.projectService.get( ProjectConstants.DEFAULT_PROJECT_NAME ) );

            final RuntimeException ex =
                Assertions.assertThrows( RuntimeException.class, () -> projectService.delete( ProjectConstants.DEFAULT_PROJECT_NAME ) );
            assertEquals( "Denied [user:system:repo-test-user] user access for [delete] operation", ex.getMessage() );
        } );
    }

    @Test
    void delete_with_roles()
    {
        final RepositoryId projectRepoId = RepositoryId.from( "com.enonic.cms.test-project" );
        doCreateProjectAsAdmin( ProjectName.from( projectRepoId ) );

        adminContext().runWith( () -> {
            this.projectService.delete( ProjectName.from( projectRepoId ) );

            assertTrue( securityService.getRole( PrincipalKey.ofRole( "cms.project.test-project.owner" ) ).isEmpty() );
            assertTrue( securityService.getRole( PrincipalKey.ofRole( "cms.project.test-project.author" ) ).isEmpty() );
            assertTrue( securityService.getRole( PrincipalKey.ofRole( "cms.project.test-project.contributor" ) ).isEmpty() );
            assertTrue( securityService.getRole( PrincipalKey.ofRole( "cms.project.test-project.editor" ) ).isEmpty() );
            assertTrue( securityService.getRole( PrincipalKey.ofRole( "cms.project.test-project.viewer" ) ).isEmpty() );

        } );
    }

    @Test
    void list()
    {
        doCreateProjectAsAdmin( ProjectName.from( "test-project1" ) );
        doCreateProjectAsAdmin( ProjectName.from( "test-project2" ) );
        doCreateProjectAsAdmin( ProjectName.from( "test-project3" ) );

        adminContext().runWith( () -> {
            assertEquals( 4, projectService.list().getSize() );

            this.projectService.delete( ProjectName.from( "test-project2" ) );
            assertEquals( 3, projectService.list().getSize() );
        } );

    }

    @Test
    void list_with_content_admin_permissions()
    {
        doCreateProjectAsAdmin( ProjectName.from( "test-project1" ) );
        doCreateProjectAsAdmin( ProjectName.from( "test-project2" ) );
        doCreateProjectAsAdmin( ProjectName.from( "test-project3" ) );

        contentAdminContext().runWith( () -> {
            final Projects projects = projectService.list();
            assertEquals( 4, projects.getSize() );
            assertTrue(
                projects.stream().anyMatch( project -> project.getName().equals( ProjectName.from( ContentConstants.CONTENT_REPO_ID ) ) ) );
        } );
    }

    @Test
    void list_with_content_manager_permissions()
    {
        doCreateProjectAsAdmin( ProjectName.from( "test-project1" ) );

        contentManagerContext().runWith( () -> {
            final Projects projects = projectService.list();
            assertEquals( 1, projects.getSize() );
            assertTrue(
                projects.stream().anyMatch( project -> project.getName().equals( ProjectName.from( ContentConstants.CONTENT_REPO_ID ) ) ) );

        } );
    }

    @Test
    void list_with_custom_permissions()
    {
        doCreateProjectAsAdmin( ProjectName.from( "test-project1" ), ProjectPermissions.create().build() );
        doCreateProjectAsAdmin( ProjectName.from( "test-project2" ),
                                ProjectPermissions.create().addOwner( REPO_TEST_OWNER.getKey() ).build() );
        doCreateProjectAsAdmin( ProjectName.from( "test-project3" ),
                                ProjectPermissions.create().addEditor( REPO_TEST_OWNER.getKey() ).build() );
        doCreateProjectAsAdmin( ProjectName.from( "test-project4" ),
                                ProjectPermissions.create().addAuthor( REPO_TEST_OWNER.getKey() ).build() );
        doCreateProjectAsAdmin( ProjectName.from( "test-project5" ),
                                ProjectPermissions.create().addContributor( REPO_TEST_OWNER.getKey() ).build() );

        final AuthenticationInfo authenticationInfo = AuthenticationInfo.copyOf( REPO_TEST_CUSTOM_MANAGER_AUTHINFO )
            .principals( ProjectAccessHelper.createRoleKey( ProjectName.from( "test-project2" ), ProjectRole.OWNER ),
                         ProjectAccessHelper.createRoleKey( ProjectName.from( "test-project3" ), ProjectRole.EDITOR ),
                         ProjectAccessHelper.createRoleKey( ProjectName.from( "test-project4" ), ProjectRole.AUTHOR ),
                         ProjectAccessHelper.createRoleKey( ProjectName.from( "test-project5" ), ProjectRole.CONTRIBUTOR ) )
            .build();

        ContextBuilder.from( contentCustomManagerContext() ).authInfo( authenticationInfo ).build().runWith( () -> {
            final Projects projects = projectService.list();

            assertEquals( 5, projectService.list().getSize() );
            assertFalse( projects.stream().anyMatch( project -> project.getName().toString().equals( "test-project1" ) ) );
            assertTrue(
                projects.stream().anyMatch( project -> project.getName().equals( ProjectName.from( ContentConstants.CONTENT_REPO_ID ) ) ) );
        } );
    }

    @Test
    void list_without_permissions()
    {
        doCreateProjectAsAdmin( ProjectName.from( "test-project1" ) );
        doCreateProjectAsAdmin( ProjectName.from( "test-project2" ) );
        doCreateProjectAsAdmin( ProjectName.from( "test-project3" ) );

        assertEquals( 0, projectService.list().getSize() );
    }

    @Test
    void get()
    {
        final Project createdProject = doCreateProjectAsAdmin( ProjectName.from( "test-project" ) );

        adminContext().runWith( () -> {
            assertProjectEquals( createdProject, projectService.get( createdProject.getName() ) );
        } );

    }

    @Test
    void get_with_admin_permissions()
    {
        final Project createdProject = doCreateProjectAsAdmin( ProjectName.from( "test-project" ) );

        contentAdminContext().runWith( () -> {
            assertProjectEquals( createdProject, projectService.get( createdProject.getName() ) );
        } );

    }

    @Test
    void get_with_manager_permissions()
    {
        final Project createdProject = doCreateProjectAsAdmin( ProjectName.from( "test-project" ) );

        contentManagerContext().runWith( () -> {
            final RuntimeException ex =
                Assertions.assertThrows( RuntimeException.class, () -> projectService.get( createdProject.getName() ) );
            assertEquals( "Denied [user:system:repo-test-user] user access to [test-project] project for [get] operation",
                          ex.getMessage() );

        } );

    }

    @Test
    void get_with_custom_permissions()
    {
        final Project createdProject = doCreateProjectAsAdmin( ProjectName.from( "test-project" ) );

        contentCustomManagerContext().runWith( () -> {
            assertProjectEquals( createdProject, projectService.get( createdProject.getName() ) );
        } );
    }

    @Test
    void get_without_permissions()
    {
        final Project createdProject = doCreateProjectAsAdmin( ProjectName.from( "test-project" ) );

        final RuntimeException ex = Assertions.assertThrows( RuntimeException.class, () -> projectService.get( createdProject.getName() ) );

        assertEquals( "Denied [user:system:test-user] user access to [test-project] project for [get] operation", ex.getMessage() );

    }

    @Test
    void get_empty_default_project_data()
    {
        adminContext().runWith( () -> {

            final Project pro = projectService.get( ProjectName.from( ContentConstants.CONTENT_REPO_ID ) );
            assertEquals( ProjectConstants.DEFAULT_PROJECT.getDescription(), pro.getDescription() );
            assertEquals( ProjectConstants.DEFAULT_PROJECT.getDisplayName(), pro.getDisplayName() );
            assertEquals( ProjectConstants.DEFAULT_PROJECT.getIcon(), pro.getIcon() );
            assertEquals( ProjectConstants.DEFAULT_PROJECT.getParents(), pro.getParents() );
        } );

    }

    @Test
    void get_default_project()
    {
        contentCustomManagerContext().runWith( () -> {
            final Project defaultProject = projectService.get( ProjectConstants.DEFAULT_PROJECT_NAME );
            assertNotNull( defaultProject );
        } );
    }

    @Test
    void modify()
    {
        doCreateProjectAsAdmin( ProjectName.from( "test-project" ) );

        adminContext().runWith( () -> {
            projectService.modify( ModifyProjectParams.create()
                                       .name( ProjectName.from( "test-project" ) )
                                       .description( "new description" )
                                       .displayName( "new display name" )
                                       .build() );

            final Project modifiedProject = projectService.get( ProjectName.from( "test-project" ) );

            assertEquals( "new description", modifiedProject.getDescription() );
            assertEquals( "new display name", modifiedProject.getDisplayName() );
        } );

    }

    @Test
    void create_parent()
    {
        adminContext().runWith( () -> {
            doCreateProject( ProjectName.from( "test-project" ), null, true, List.of( ProjectName.from( "parent" ) ) );
            final Project modifiedProject = projectService.get( ProjectName.from( "test-project" ) );

            assertEquals( ProjectName.from( "parent" ), modifiedProject.getParents().get( 0 ) );
        } );
    }

    @Test
    void modify_icon()
    {
        doCreateProjectAsAdmin( ProjectName.from( "test-project" ) );

        adminContext().runWith( () -> {
            projectService.modifyIcon( ModifyProjectIconParams.create()
                                           .name( ProjectName.from( "test-project" ) )
                                           .icon( CreateAttachment.create()
                                                      .mimeType( "image/png" )
                                                      .label( "My New Image" )
                                                      .name( "MyNewImage.png" )
                                                      .byteSource( ByteSource.wrap( "new bytes".getBytes() ) )
                                                      .build() )
                                           .scaleWidth( 0 )
                                           .build() );

            Project modifiedProject = projectService.get( ProjectName.from( "test-project" ) );

            assertEquals( "image/png", modifiedProject.getIcon().getMimeType() );
            assertEquals( "My New Image", modifiedProject.getIcon().getLabel() );
            assertEquals( "MyNewImage.png", modifiedProject.getIcon().getName() );

            projectService.modifyIcon(
                ModifyProjectIconParams.create().name( ProjectName.from( "test-project" ) ).icon( null ).scaleWidth( 0 ).build() );

            modifiedProject = projectService.get( ProjectName.from( "test-project" ) );

            assertNull( modifiedProject.getIcon() );
        } );
    }

    @Test
    void get_icon()
        throws Exception
    {
        final Project project = doCreateProjectAsAdmin( ProjectName.from( "test-project" ) );

        final ByteSource source = ByteSource.wrap( "new bytes".getBytes() );
        adminContext().runWith( () -> {
            assertNull( projectService.getIcon( project.getName() ) );

            projectService.modifyIcon( ModifyProjectIconParams.create()
                                           .name( project.getName() )
                                           .icon( CreateAttachment.create()
                                                      .mimeType( "image/png" )
                                                      .label( "My New Image" )
                                                      .name( "MyNewImage.png" )
                                                      .byteSource( ByteSource.wrap( "new bytes".getBytes() ) )
                                                      .build() )
                                           .build() );

            try
            {
                assertArrayEquals( source.read(), projectService.getIcon( project.getName() ).read() );
            }
            catch ( IOException e )
            {
                throw new UncheckedIOException( e );
            }
        } );

        Assertions.assertThrows( ProjectAccessException.class, () -> projectService.getIcon( project.getName() ) );
    }

    @Test
    void modify_with_deleted_role()
    {
        final RepositoryId projectRepoId = RepositoryId.from( "com.enonic.cms.test-project" );
        doCreateProjectAsAdmin( ProjectName.from( projectRepoId ) );

        adminContext().runWith( () -> {
            securityService.deletePrincipal( PrincipalKey.ofRole( "cms.project.test-project.owner" ) );

            projectService.modify( ModifyProjectParams.create()
                                       .name( ProjectName.from( "test-project" ) )
                                       .description( "new description" )
                                       .displayName( "new display name" )
                                       .build() );
        } );

        assertFalse( securityService.getRole( PrincipalKey.ofRole( "cms.project.test-project.owner" ) ).isPresent() );
    }

    @Test
    void modify_with_applications()
    {
        final RepositoryId projectRepoId = RepositoryId.from( "com.enonic.cms.test-project" );

        final PropertyTree config1 = new PropertyTree();
        config1.addString( "a", "value1" );

        final PropertyTree config2 = new PropertyTree();
        config2.addString( "a", "value2" );

        adminContext().callWith( () -> doCreateProject( ProjectName.from( projectRepoId ), SiteConfigs.from(
            SiteConfig.create().application( ApplicationKey.from( "app" ) ).config( config1 ).build() ) ) );

        final Project project = adminContext().callWith( () -> projectService.modify( ModifyProjectParams.create()
                                                                                          .name( ProjectName.from( "test-project" ) )
                                                                                          .displayName( "project name" )
                                                                                          .addSiteConfig( SiteConfig.create()
                                                                                                              .application(
                                                                                                                  ApplicationKey.from(
                                                                                                                      "app" ) )
                                                                                                              .config( config2 )
                                                                                                              .build() )
                                                                                          .build() ) );

        assertEquals( 1, project.getSiteConfigs().getSize() );
        assertTrue(
            project.getSiteConfigs().stream().anyMatch( config -> config.getApplicationKey().equals( ApplicationKey.from( "app" ) ) ) );
        assertEquals( config2, project.getSiteConfigs().get( ApplicationKey.from( "app" ) ).getConfig() );
    }

    @Test
    void get_permissions_wrong_project()
    {
        Assertions.assertThrows( ProjectAccessException.class, () -> projectService.getPermissions( ProjectName.from( "test-project" ) ) );
    }

    @Test
    void get_permissions_default_project()
    {
        final RuntimeException ex =
            Assertions.assertThrows( RuntimeException.class, () -> projectService.getPermissions( ProjectConstants.DEFAULT_PROJECT_NAME ) );

        assertEquals( "Default project has no roles.", ex.getMessage() );
    }

    @Test
    void modify_permissions()
    {
        final RepositoryId projectRepoId = RepositoryId.from( "com.enonic.cms.test-project" );

        adminContext().runWith( () -> {
            final User user1 = securityService.createUser( CreateUserParams.create()
                                                               .userKey( PrincipalKey.ofUser( IdProviderKey.system(), "user1" ) )
                                                               .displayName( "user1" )
                                                               .login( "user1" )
                                                               .build() );

            final User user2 = securityService.createUser( CreateUserParams.create()
                                                               .userKey( PrincipalKey.ofUser( IdProviderKey.system(), "user2" ) )
                                                               .displayName( "user2" )
                                                               .login( "user2" )
                                                               .build() );

            doCreateProjectAsAdmin( ProjectName.from( projectRepoId ), ProjectPermissions.create().addOwner( user1.getKey() ).build() );

            projectService.modifyPermissions( ProjectName.from( "test-project" ),
                                              ProjectPermissions.create().addOwner( user2.getKey() ).build() );

            final PrincipalRelationships principalRelationships =
                securityService.getRelationships( PrincipalKey.ofRole( "cms.project.test-project.owner" ) );

            assertEquals( 1, principalRelationships.getSize() );
            assertEquals( principalRelationships.get( 0 ).getTo(), user2.getKey() );
        } );
    }

    @Test
    void modify_default_project_permissions()
    {
        adminContext().runWith( () -> {
            final User user1 = securityService.createUser( CreateUserParams.create()
                                                               .userKey( PrincipalKey.ofUser( IdProviderKey.system(), "user1" ) )
                                                               .displayName( "user1" )
                                                               .login( "user1" )
                                                               .build() );

            final RuntimeException ex = Assertions.assertThrows( RuntimeException.class, () -> projectService.modifyPermissions(
                ProjectConstants.DEFAULT_PROJECT_NAME, ProjectPermissions.create().addOwner( user1.getKey() ).build() ) );

            assertEquals( "Default project permissions cannot be modified.", ex.getMessage() );

        } );
    }

    @Test
    void graph()
    {
        final Project project1 = adminContext().callWith( () -> doCreateProject( ProjectName.from( "project1" ), null, true, null ) );
        final Project project2 =
            adminContext().callWith( () -> doCreateProject( ProjectName.from( "project2" ), null, true, List.of( project1.getName() ) ) );
        final Project project3 =
            adminContext().callWith( () -> doCreateProject( ProjectName.from( "project3" ), null, true, List.of( project2.getName() ) ) );
        final Project project4 =
            adminContext().callWith( () -> doCreateProject( ProjectName.from( "project4" ), null, true, List.of( project2.getName() ) ) );
        final Project project5 =
            adminContext().callWith( () -> doCreateProject( ProjectName.from( "project5" ), null, true, List.of( project4.getName() ) ) );

        final ProjectGraph graph1 = adminContext().callWith( () -> projectService.graph( project1.getName() ) );

        assertEquals( 5, graph1.getSize() );
        assertThat( graph1.getList() ).extracting( "name", "parents" )
            .containsExactly( tuple( project1.getName(), List.of() ), tuple( project2.getName(), List.of( project1.getName() ) ),
                              tuple( project4.getName(), List.of( project2.getName() ) ),
                              tuple( project3.getName(), List.of( project2.getName() ) ),
                              tuple( project5.getName(), List.of( project4.getName() ) ) );

        final ProjectGraph graph2 = adminContext().callWith( () -> projectService.graph( project4.getName() ) );

        assertEquals( 4, graph2.getSize() );
        assertThat( graph2.getList() ).extracting( "name", "parents" )
            .containsExactly( tuple( project1.getName(), List.of() ), tuple( project2.getName(), List.of( project1.getName() ) ),
                              tuple( project4.getName(), List.of( project2.getName() ) ),
                              tuple( project5.getName(), List.of( project4.getName() ) ) );
    }

    @Test
    void graph_without_permissions()
    {
        final Project project1 = doCreateProjectAsAdmin( ProjectName.from( "project1" ) );

        assertThrows( ProjectNotFoundException.class, () -> projectService.graph( ProjectName.from( "project1" ) ) );

        final Project project2 =
            adminContext().callWith( () -> doCreateProject( ProjectName.from( "project2" ), null, true, List.of( project1.getName() ) ) );
        final Project project3 =
            adminContext().callWith( () -> doCreateProject( ProjectName.from( "project3" ), null, true, List.of( project2.getName() ) ) );
        final Project project4 =
            adminContext().callWith( () -> doCreateProject( ProjectName.from( "project4" ), null, true, List.of( project2.getName() ) ) );
        final Project project5 =
            adminContext().callWith( () -> doCreateProject( ProjectName.from( "project5" ), null, true, List.of( project4.getName() ) ) );

        contextWithAuthInfo( getAccessForProject( ProjectName.from( "project4" ), List.of( ProjectRole.VIEWER ) ) ).runWith( () -> {
            assertEquals( 4, projectService.graph( ProjectName.from( "project4" ) ).getSize() );
        } );

    }

    @Test
    void get_available_applications()
    {
        final RepositoryId parentRepoId = RepositoryId.from( "com.enonic.cms.parent" );
        final RepositoryId childRepoId = RepositoryId.from( "com.enonic.cms.child" );

        final Project parent = adminContext().callWith( () -> doCreateProject( ProjectName.from( parentRepoId ), SiteConfigs.from(
            SiteConfig.create().application( ApplicationKey.from( "app1" ) ).config( new PropertyTree() ).build(),
            SiteConfig.create().application( ApplicationKey.from( "app2" ) ).config( new PropertyTree() ).build() ) ) );

        final Project child = adminContext().callWith( () -> doCreateProject( ProjectName.from( childRepoId ), parent.getName(),
                                                                              SiteConfigs.from( SiteConfig.create()
                                                                                                    .application(
                                                                                                        ApplicationKey.from( "app2" ) )
                                                                                                    .config( new PropertyTree() )
                                                                                                    .build(), SiteConfig.create()
                                                                                                    .application(
                                                                                                        ApplicationKey.from( "app3" ) )
                                                                                                    .config( new PropertyTree() )
                                                                                                    .build() ) ) );

        final ApplicationKeys parentAvailableApplications =
            adminContext().callWith( () -> projectService.getAvailableApplications( parent.getName() ) );
        final ApplicationKeys childAvailableApplications =
            adminContext().callWith( () -> projectService.getAvailableApplications( child.getName() ) );

        assertEquals( 2, parentAvailableApplications.getSize() );
        assertEquals( "app1", parentAvailableApplications.get( 0 ).toString() );
        assertEquals( "app2", parentAvailableApplications.get( 1 ).toString() );

        assertEquals( 3, childAvailableApplications.getSize() );
        assertEquals( "app2", childAvailableApplications.get( 0 ).toString() );
        assertEquals( "app3", childAvailableApplications.get( 1 ).toString() );
        assertEquals( "app1", childAvailableApplications.get( 2 ).toString() );
    }

    @Test
    void get_available_applications_without_permissions()
    {
        final RepositoryId parentRepoId = RepositoryId.from( "com.enonic.cms.parent" );
        final RepositoryId childRepoId = RepositoryId.from( "com.enonic.cms.child" );

        final Project parent = adminContext().callWith( () -> doCreateProject( ProjectName.from( parentRepoId ), SiteConfigs.from(
            SiteConfig.create().application( ApplicationKey.from( "app1" ) ).config( new PropertyTree() ).build(),
            SiteConfig.create().application( ApplicationKey.from( "app2" ) ).config( new PropertyTree() ).build() ) ) );

        final Project child = adminContext().callWith( () -> doCreateProject( ProjectName.from( childRepoId ), parent.getName(),
                                                                              SiteConfigs.from( SiteConfig.create()
                                                                                                    .application(
                                                                                                        ApplicationKey.from( "app2" ) )
                                                                                                    .config( new PropertyTree() )
                                                                                                    .build(), SiteConfig.create()
                                                                                                    .application(
                                                                                                        ApplicationKey.from( "app3" ) )
                                                                                                    .config( new PropertyTree() )
                                                                                                    .build() ) ) );

        final ApplicationKeys parentAvailableApplications =
            contextWithAuthInfo( getAccessForProject( parent.getName(), List.of( ProjectRole.VIEWER ) ) ).callWith(
                () -> projectService.getAvailableApplications( parent.getName() ) );

        final ApplicationKeys childAvailableApplications =
            contextWithAuthInfo( getAccessForProject( child.getName(), List.of( ProjectRole.VIEWER ) ) ).callWith(
                () -> projectService.getAvailableApplications( child.getName() ) );

        assertEquals( 2, parentAvailableApplications.getSize() );
        assertEquals( "app1", parentAvailableApplications.get( 0 ).toString() );
        assertEquals( "app2", parentAvailableApplications.get( 1 ).toString() );

        assertEquals( 3, childAvailableApplications.getSize() );
        assertEquals( "app2", childAvailableApplications.get( 0 ).toString() );
        assertEquals( "app3", childAvailableApplications.get( 1 ).toString() );
        assertEquals( "app1", childAvailableApplications.get( 2 ).toString() );
    }

    @Test
    void get_available_applications_null_project()
    {
        assertThrows( ProjectNotFoundException.class,
                      () -> adminContext().callWith( () -> projectService.getAvailableApplications( ProjectName.from( "unknown" ) ) ) );
    }

    @Test
    void create_with_circle_dependency()
    {
        when( projectConfig.multiInheritance() ).thenReturn( true );

        adminContext().runWith( () -> {
            final Project parent = doCreateProject( ProjectName.from( "parent" ), SiteConfigs.empty() );
            final Project child = doCreateProject( ProjectName.from( "child" ), parent.getName(), SiteConfigs.empty() );
            final Project grandchild = doCreateProject( ProjectName.from( "grandchild" ), ProjectPermissions.create().build(), false,
                                                        List.of( ProjectName.from( "invalid" ), child.getName() ) );

            this.projectService.delete( parent.getName() );

            assertThrows( ProjectCircleDependencyException.class,
                          () -> doCreateProject( ProjectName.from( "parent" ), grandchild.getName(), SiteConfigs.empty() ) );
        } );
    }

    @Test
    void test_enabled_multiple_parents()
    {
        when( projectConfig.multiInheritance() ).thenReturn( true );

        adminContext().runWith( () -> {
            final Project parent1 = doCreateProject( ProjectName.from( "parent1" ), SiteConfigs.empty() );
            final Project parent2 = doCreateProject( ProjectName.from( "parent2" ), SiteConfigs.empty() );
            final Project child = doCreateProject( ProjectName.from( "child" ), ProjectPermissions.create().build(), false,
                                                   List.of( parent1.getName(), parent2.getName() ) );

            assertEquals( List.of( parent1.getName(), parent2.getName() ), child.getParents() );
        } );
    }

    @Test
    void test_disabled_multiple_parents()
    {

        adminContext().runWith( () -> {
            final Project parent1 = doCreateProject( ProjectName.from( "parent1" ), SiteConfigs.empty() );
            final Project parent2 = doCreateProject( ProjectName.from( "parent2" ), SiteConfigs.empty() );

            assertThrows( ProjectMultipleParentsException.class, () ->

                doCreateProject( ProjectName.from( "child" ), ProjectPermissions.create().build(), false,
                                 List.of( parent1.getName(), parent2.getName() ) ) );
        } );
    }

    private Project doCreateProjectAsAdmin( final ProjectName name )
    {
        return adminContext().callWith( () -> doCreateProject( name ) );

    }

    private Project doCreateProjectAsAdmin( final ProjectName name, final ProjectPermissions projectPermissions )
    {
        return adminContext().callWith( () -> doCreateProject( name, projectPermissions ) );
    }

    private Project doCreateProject( final ProjectName name )
    {
        return doCreateProject( name, ProjectPermissions.create()
            .addOwner( REPO_TEST_OWNER.getKey() )
            .addViewer( PrincipalKey.from( "user:system:custom1" ) )
            .addViewer( PrincipalKey.from( "user:system:custom2" ) )
            .build() );
    }

    private Project doCreateProject( final ProjectName name, final ProjectPermissions projectPermissions )
    {
        return this.doCreateProject( name, projectPermissions, false, null );
    }

    private Project doCreateProject( final ProjectName name, final String displayName, final String description )
    {
        return this.projectService.create(
            CreateProjectParams.create().name( name ).description( description ).displayName( displayName ).build() );
    }

    private Project doCreateProject( final ProjectName name, final ProjectPermissions projectPermissions, final boolean forceInitialization,
                                     final Collection<ProjectName> parents )
    {
        return doCreateProject( name, projectPermissions, forceInitialization, parents, null, null );
    }

    private Project doCreateProject( final ProjectName name, final ProjectPermissions projectPermissions, final boolean forceInitialization,
                                     final Collection<ProjectName> parents, final AccessControlList permissions,
                                     final SiteConfigs siteConfigs )
    {
        final CreateProjectParams.Builder params = CreateProjectParams.create()
            .name( name )
            .description( "description" )
            .displayName( "Project display name" )
            .permissions( permissions )
            .forceInitialization( forceInitialization );

        if ( parents != null && !parents.isEmpty() )
        {
            params.addParents( parents );
        }

        if ( siteConfigs != null )
        {
            siteConfigs.forEach( params::addSiteConfig );
        }

        final Project project = projectService.create( params.build() );

        if ( projectPermissions != null )
        {
            this.projectService.modifyPermissions( name, projectPermissions );
        }

        return project;
    }

    private Project doCreateProject( final ProjectName name, final ProjectName parent, final SiteConfigs siteConfigs )
    {
        return this.doCreateProject( name, null, false, List.of( parent ), null, siteConfigs );
    }

    private Project doCreateProject( final ProjectName name, final SiteConfigs siteConfigs )
    {
        return this.doCreateProject( name, null, false, null, null, siteConfigs );
    }

    private void assertProjectEquals( final Project p1, final Project p2 )
    {
        assertAll( () -> assertEquals( p1.getName(), p2.getName() ), () -> assertEquals( p1.getDescription(), p2.getDescription() ),
                   () -> assertEquals( p1.getDisplayName(), p2.getDisplayName() ), () -> assertEquals( p1.getIcon(), p2.getIcon() ) );
    }

    private AuthenticationInfo getAccessForProject( final ProjectName projectName, final List<ProjectRole> roles )
    {
        final AuthenticationInfo.Builder authenticationInfo = AuthenticationInfo.create()
            .principals( RoleKeys.AUTHENTICATED )
            .principals( RoleKeys.CONTENT_MANAGER_APP )
            .user( REPO_TEST_OWNER );

        roles.forEach( role -> authenticationInfo.principals( ProjectAccessHelper.createRoleKey( projectName, role ) ) );

        return authenticationInfo.build();
    }

}
