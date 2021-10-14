package com.enonic.xp.core.project;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;

import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.impl.project.ProjectAccessException;
import com.enonic.xp.core.impl.project.ProjectAccessHelper;
import com.enonic.xp.core.impl.project.ProjectPermissionsContextManagerImpl;
import com.enonic.xp.core.impl.project.ProjectServiceImpl;
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
import com.enonic.xp.project.ProjectPermissions;
import com.enonic.xp.project.ProjectRole;
import com.enonic.xp.project.Projects;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.index.IndexServiceInternal;
import com.enonic.xp.repo.impl.node.AbstractNodeTest;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class ProjectServiceImplTest
    extends AbstractNodeTest
{
    private static final User REPO_TEST_DEFAULT_USER =
        User.create().key( PrincipalKey.ofUser( IdProviderKey.system(), "repo-test-user" ) ).login( "repo-test-user" ).build();

    private static final User REPO_TEST_OWNER =
        User.create().key( PrincipalKey.ofUser( IdProviderKey.system(), "custom-user" ) ).login( "custom-user" ).build();

    private static final AuthenticationInfo REPO_TEST_ADMIN_USER_AUTHINFO = AuthenticationInfo.create().
        principals( RoleKeys.AUTHENTICATED ).
        principals( RoleKeys.ADMIN ).
        user( REPO_TEST_DEFAULT_USER ).
        build();

    private static final AuthenticationInfo REPO_TEST_CONTENT_ADMIN_AUTHINFO = AuthenticationInfo.create().
        principals( RoleKeys.AUTHENTICATED ).
        principals( RoleKeys.CONTENT_MANAGER_ADMIN ).
        user( REPO_TEST_DEFAULT_USER ).
        build();

    private static final AuthenticationInfo REPO_TEST_CONTENT_MANAGER_AUTHINFO = AuthenticationInfo.create().
        principals( RoleKeys.AUTHENTICATED ).
        principals( RoleKeys.CONTENT_MANAGER_APP ).
        user( REPO_TEST_DEFAULT_USER ).
        build();

    private static final AuthenticationInfo REPO_TEST_CUSTOM_MANAGER_AUTHINFO = AuthenticationInfo.create().
        principals( RoleKeys.AUTHENTICATED ).
        principals( RoleKeys.CONTENT_MANAGER_APP ).
        principals( ProjectAccessHelper.createRoleKey( ProjectName.from( "test-project" ), ProjectRole.OWNER ) ).
        user( REPO_TEST_OWNER ).
        build();

    private static Context adminContext()
    {
        return ContextBuilder.create().
            branch( "master" ).
            repositoryId( SystemConstants.SYSTEM_REPO_ID ).
            authInfo( REPO_TEST_ADMIN_USER_AUTHINFO ).
            build();
    }

    private static Context contentAdminContext()
    {
        return ContextBuilder.create().
            branch( "master" ).
            repositoryId( SystemConstants.SYSTEM_REPO_ID ).
            authInfo( REPO_TEST_CONTENT_ADMIN_AUTHINFO ).
            build();
    }

    private static Context contentManagerContext()
    {
        return ContextBuilder.create().
            branch( "master" ).
            repositoryId( SystemConstants.SYSTEM_REPO_ID ).
            authInfo( REPO_TEST_CONTENT_MANAGER_AUTHINFO ).
            build();
    }

    private static Context contentCustomManagerContext()
    {
        return ContextBuilder.create().
            branch( "master" ).
            repositoryId( SystemConstants.SYSTEM_REPO_ID ).
            authInfo( REPO_TEST_CUSTOM_MANAGER_AUTHINFO ).
            build();
    }

    private ProjectServiceImpl projectService;

    private SecurityServiceImpl securityService;

    @BeforeEach
    protected void setUpNode()
        throws Exception
    {
        super.setUpNode();

        securityService = new SecurityServiceImpl( this.nodeService, indexService );

        adminContext().runWith( () -> {
            securityService.initialize();

            final ProjectPermissionsContextManagerImpl projectAccessContextManager = new ProjectPermissionsContextManagerImpl();

            projectService =
                new ProjectServiceImpl( repositoryService, indexService, nodeService, securityService, projectAccessContextManager,
                                        eventPublisher );
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
        IndexServiceInternal indexServiceInternalMock = Mockito.mock( IndexServiceInternal.class );
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

        final Project project = contentAdminContext().
            callWith( () -> doCreateProject( ProjectName.from( projectRepoId ) ) );

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
            final User user1 = securityService.createUser( CreateUserParams.create().
                userKey( PrincipalKey.ofUser( IdProviderKey.system(), "user1" ) ).
                displayName( "user1" ).
                login( "user1" ).
                build() );

            final User user2 = securityService.createUser( CreateUserParams.create().
                userKey( PrincipalKey.ofUser( IdProviderKey.system(), "user2" ) ).
                displayName( "user2" ).
                login( "user2" ).
                build() );

            doCreateProjectAsAdmin( ProjectName.from( projectRepoId ), ProjectPermissions.create().
                addOwner( user1.getKey() ).
                addOwner( user2.getKey() ).
                build() );

            final Set<PrincipalKey> members = securityService.getRelationships( PrincipalKey.ofRole( "cms.project.test-project.owner" ) ).
                stream().
                map( PrincipalRelationship::getTo ).collect( Collectors.toSet() );

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

        List.of( ContextBuilder.from( adminContext() ).
            branch( ContentConstants.BRANCH_DRAFT ).
            repositoryId( projectRepoId ).
            build(), ContextBuilder.from( adminContext() ).
            branch( ContentConstants.BRANCH_MASTER ).
            repositoryId( projectRepoId ).
            build() ).
            forEach( context -> context.runWith( () -> {

                final Node rootContentNode = nodeService.getByPath( ContentConstants.CONTENT_ROOT_PATH );
                final AccessControlList rootContentPermissions = rootContentNode.getPermissions();

                assertTrue( rootContentPermissions.getEntry( RoleKeys.ADMIN ).isAllowedAll() );
                assertTrue( rootContentPermissions.getEntry( RoleKeys.CONTENT_MANAGER_ADMIN ).isAllowedAll() );
                assertTrue( rootContentPermissions.getEntry( PrincipalKey.ofRole( "cms.project.test-project.owner" ) ).isAllowedAll() );
                assertTrue( rootContentPermissions.getEntry( PrincipalKey.ofRole( "cms.project.test-project.editor" ) ).isAllowedAll() );
                assertTrue(
                    rootContentPermissions.getEntry( PrincipalKey.ofRole( "cms.project.test-project.author" ) ).isAllowed( Permission.READ,
                                                                                                                           Permission.CREATE,
                                                                                                                           Permission.MODIFY,
                                                                                                                           Permission.DELETE ) );
                assertTrue( rootContentPermissions.getEntry( PrincipalKey.ofRole( "cms.project.test-project.contributor" ) ).
                    isAllowed( Permission.READ ) );
                assertTrue( rootContentPermissions.getEntry( PrincipalKey.ofRole( "cms.project.test-project.viewer" ) ).
                    isAllowed( Permission.READ ) );
                assertNull( rootContentPermissions.getEntry( RoleKeys.EVERYONE ) );
            } ) );
    }

    @Test
    void create_with_root_issues_permissions()
    {
        final RepositoryId projectRepoId = RepositoryId.from( "com.enonic.cms.test-project" );
        final ProjectName projectName = ProjectName.from( projectRepoId );

        doCreateProjectAsAdmin( projectName );

        ContextBuilder.from( adminContext() ).
            branch( ContentConstants.BRANCH_DRAFT ).
            repositoryId( projectRepoId ).
            build().runWith( () -> {

            final Node rootIssuesNode = nodeService.getByPath( NodePath.create( NodePath.ROOT, "issues" ).build() );
            final AccessControlList rootContentPermissions = rootIssuesNode.getPermissions();

            assertAll( () -> assertTrue( rootContentPermissions.getEntry( RoleKeys.ADMIN ).isAllowedAll() ),
                       () -> assertTrue( rootContentPermissions.getEntry( RoleKeys.CONTENT_MANAGER_ADMIN ).isAllowedAll() ),
                       () -> assertTrue( rootContentPermissions.isAllowedFor( PrincipalKey.ofRole( "cms.project.test-project.viewer" ),
                                                                              Permission.READ ) ) );

            PrincipalKeys.from( PrincipalKey.ofRole( "cms.project.test-project.owner" ),
                                PrincipalKey.ofRole( "cms.project.test-project.editor" ),
                                PrincipalKey.ofRole( "cms.project.test-project.contributor" ),
                                PrincipalKey.ofRole( "cms.project.test-project.author" ) ).
                forEach( principalKey -> assertTrue(
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
            .build() ) );

        ContextBuilder.from( adminContext() ).
            branch( ContentConstants.BRANCH_DRAFT ).
            repositoryId( projectRepoId ).
            build().runWith( () -> {

            final Node rootContentNode = nodeService.getByPath( ContentConstants.CONTENT_ROOT_PATH );
            final AccessControlList rootContentPermissions = rootContentNode.getPermissions();

            assertTrue( rootContentPermissions.getEntry( RoleKeys.EVERYONE ).isAllowed( Permission.READ ) );
        } );
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

        final AuthenticationInfo authenticationInfo = AuthenticationInfo.copyOf( REPO_TEST_CUSTOM_MANAGER_AUTHINFO ).
            principals( ProjectAccessHelper.createRoleKey( ProjectName.from( "test-project2" ), ProjectRole.OWNER ),
                        ProjectAccessHelper.createRoleKey( ProjectName.from( "test-project3" ), ProjectRole.EDITOR ),
                        ProjectAccessHelper.createRoleKey( ProjectName.from( "test-project4" ), ProjectRole.AUTHOR ),
                        ProjectAccessHelper.createRoleKey( ProjectName.from( "test-project5" ), ProjectRole.CONTRIBUTOR ) ).
            build();

        ContextBuilder.from( contentCustomManagerContext() ).
            authInfo( authenticationInfo ).
            build().
            runWith( () -> {
                final Projects projects = projectService.list();

                assertEquals( 5, projectService.list().getSize() );
                assertFalse( projects.stream().anyMatch( project -> project.getName().toString().equals( "test-project1" ) ) );
                assertTrue( projects.stream().anyMatch(
                    project -> project.getName().equals( ProjectName.from( ContentConstants.CONTENT_REPO_ID ) ) ) );
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
            assertEquals( ProjectConstants.DEFAULT_PROJECT, pro );
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
            projectService.modify( ModifyProjectParams.create().
                name( ProjectName.from( "test-project" ) ).
                description( "new description" ).
                displayName( "new display name" ).
                build() );

            final Project modifiedProject = projectService.get( ProjectName.from( "test-project" ) );

            assertEquals( "new description", modifiedProject.getDescription() );
            assertEquals( "new display name", modifiedProject.getDisplayName() );
        } );

    }

    @Test
    void create_parent()
    {

        adminContext().runWith( () -> {
            doCreateProject( ProjectName.from( "test-project" ), null, true, ProjectName.from( "parent" ) );
            final Project modifiedProject = projectService.get( ProjectName.from( "test-project" ) );

            assertEquals( ProjectName.from( "parent" ), modifiedProject.getParent() );
        } );
    }

    @Test
    void modify_icon()
    {
        doCreateProjectAsAdmin( ProjectName.from( "test-project" ) );

        adminContext().runWith( () -> {
            projectService.modifyIcon( ModifyProjectIconParams.create().
                name( ProjectName.from( "test-project" ) ).
                icon( CreateAttachment.create().
                    mimeType( "image/png" ).
                    label( "My New Image" ).
                    name( "MyNewImage.png" ).
                    byteSource( ByteSource.wrap( "new bytes".getBytes() ) ).
                    build() ).
                scaleWidth( 0 ).
                build() );

            Project modifiedProject = projectService.get( ProjectName.from( "test-project" ) );

            assertEquals( "image/png", modifiedProject.getIcon().getMimeType() );
            assertEquals( "My New Image", modifiedProject.getIcon().getLabel() );
            assertEquals( "MyNewImage.png", modifiedProject.getIcon().getName() );

            projectService.modifyIcon( ModifyProjectIconParams.create().
                name( ProjectName.from( "test-project" ) ).
                icon( null ).
                scaleWidth( 0 ).
                build() );

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

            projectService.modifyIcon( ModifyProjectIconParams.create().
                name( project.getName() ).
                icon( CreateAttachment.create().
                    mimeType( "image/png" ).
                    label( "My New Image" ).
                    name( "MyNewImage.png" ).
                    byteSource( ByteSource.wrap( "new bytes".getBytes() ) ).
                    build() ).
                build() );

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

            projectService.modify( ModifyProjectParams.create().
                name( ProjectName.from( "test-project" ) ).
                description( "new description" ).
                displayName( "new display name" ).
                build() );
        } );

        assertFalse( securityService.getRole( PrincipalKey.ofRole( "cms.project.test-project.owner" ) ).isPresent() );
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
            final User user1 = securityService.createUser( CreateUserParams.create().
                userKey( PrincipalKey.ofUser( IdProviderKey.system(), "user1" ) ).
                displayName( "user1" ).
                login( "user1" ).
                build() );

            final User user2 = securityService.createUser( CreateUserParams.create().
                userKey( PrincipalKey.ofUser( IdProviderKey.system(), "user2" ) ).
                displayName( "user2" ).
                login( "user2" ).
                build() );

            doCreateProjectAsAdmin( ProjectName.from( projectRepoId ), ProjectPermissions.create().
                addOwner( user1.getKey() ).
                build() );

            projectService.modifyPermissions( ProjectName.from( "test-project" ), ProjectPermissions.create().
                addOwner( user2.getKey() ).
                build() );

            final PrincipalRelationships principalRelationships =
                securityService.getRelationships( PrincipalKey.ofRole( "cms.project.test-project.owner" ) );

            assertEquals( 1, principalRelationships.getSize() );
            assertEquals( principalRelationships.
                get( 0 ).
                getTo(), user2.getKey() );
        } );
    }

    @Test
    void modify_default_project_permissions()
    {
        adminContext().runWith( () -> {
            final User user1 = securityService.createUser( CreateUserParams.create().
                userKey( PrincipalKey.ofUser( IdProviderKey.system(), "user1" ) ).
                displayName( "user1" ).
                login( "user1" ).
                build() );

            final RuntimeException ex = Assertions.assertThrows( RuntimeException.class, () -> projectService.modifyPermissions(
                ProjectConstants.DEFAULT_PROJECT_NAME, ProjectPermissions.create().
                    addOwner( user1.getKey() ).
                    build() ) );

            assertEquals( "Default project permissions cannot be modified.", ex.getMessage() );

        } );
    }

    @Test
    void graph()
    {
        final Project project1 = adminContext().callWith( () -> doCreateProject( ProjectName.from( "project1" ), null, true, null ) );
        final Project project2 =
            adminContext().callWith( () -> doCreateProject( ProjectName.from( "project2" ), null, true, project1.getName() ) );
        final Project project3 =
            adminContext().callWith( () -> doCreateProject( ProjectName.from( "project3" ), null, true, project2.getName() ) );
        final Project project4 =
            adminContext().callWith( () -> doCreateProject( ProjectName.from( "project4" ), null, true, project2.getName() ) );
        final Project project5 =
            adminContext().callWith( () -> doCreateProject( ProjectName.from( "project5" ), null, true, project4.getName() ) );

        final ProjectGraph graph1 = adminContext().callWith( () -> projectService.graph( project1.getName() ) );

        assertEquals( 5, graph1.getSize() );
        assertThat( graph1.getList() ).
            extracting( "name", "parent" ).
            containsExactly( tuple( project1.getName(), null ), tuple( project2.getName(), project1.getName() ),
                             tuple( project4.getName(), project2.getName() ), tuple( project3.getName(), project2.getName() ),
                             tuple( project5.getName(), project4.getName() ) );

        final ProjectGraph graph2 = adminContext().callWith( () -> projectService.graph( project4.getName() ) );

        assertEquals( 4, graph2.getSize() );
        assertThat( graph2.getList() ).
            extracting( "name", "parent" ).
            containsExactly( tuple( project1.getName(), null ), tuple( project2.getName(), project1.getName() ),
                             tuple( project4.getName(), project2.getName() ), tuple( project5.getName(), project4.getName() ) );
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
        return doCreateProject( name, ProjectPermissions.create().
            addOwner( REPO_TEST_OWNER.getKey() ).
            addViewer( PrincipalKey.from( "user:system:custom1" ) ).
            addViewer( PrincipalKey.from( "user:system:custom2" ) ).
            build() );
    }

    private Project doCreateProject( final ProjectName name, final ProjectPermissions projectPermissions )
    {
        return this.doCreateProject( name, projectPermissions, false, null );
    }

    private Project doCreateProject( final ProjectName name, final String displayName, final String description )
    {
        return this.projectService.create( CreateProjectParams.create().
            name( name ).
            description( description ).
            displayName( displayName ).
            build() );
    }

    private Project doCreateProject( final ProjectName name, final ProjectPermissions projectPermissions, final boolean forceInitialization,
                                     final ProjectName parent )
    {
        return doCreateProject( name, projectPermissions, forceInitialization, parent, null );
    }

    private Project doCreateProject( final ProjectName name, final ProjectPermissions projectPermissions, final boolean forceInitialization,
                                     final ProjectName parent, final AccessControlList permissions )
    {
        final Project project = this.projectService.create( CreateProjectParams.create().
            name( name ).
            description( "description" ).
            displayName( "Project display name" ).
            parent( parent ).
            permissions( permissions ).
            forceInitialization( forceInitialization ).
            build() );

        if ( projectPermissions != null )
        {
            this.projectService.modifyPermissions( name, projectPermissions );
        }

        return project;
    }

    private void assertProjectEquals( final Project p1, final Project p2 )
    {
        assertAll( () -> assertEquals( p1.getName(), p2.getName() ), () -> assertEquals( p1.getDescription(), p2.getDescription() ),
                   () -> assertEquals( p1.getDisplayName(), p2.getDisplayName() ), () -> assertEquals( p1.getIcon(), p2.getIcon() ) );
    }

}
