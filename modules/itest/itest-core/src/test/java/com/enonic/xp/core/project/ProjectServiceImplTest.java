package com.enonic.xp.core.project;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.io.ByteSource;

import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.impl.project.ProjectPermissionsContextManagerImpl;
import com.enonic.xp.core.impl.project.ProjectServiceImpl;
import com.enonic.xp.core.impl.security.SecurityServiceImpl;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.project.CreateProjectParams;
import com.enonic.xp.project.ModifyProjectParams;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectPermissions;
import com.enonic.xp.project.ProjectRole;
import com.enonic.xp.project.Projects;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.index.IndexServiceImpl;
import com.enonic.xp.repo.impl.node.AbstractNodeTest;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.CreateUserParams;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalRelationship;
import com.enonic.xp.security.PrincipalRelationships;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.security.User;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        user( REPO_TEST_OWNER ).
        build();

    private final static Context ADMIN_CONTEXT = ContextBuilder.create().
        branch( "master" ).
        repositoryId( SystemConstants.SYSTEM_REPO.getId() ).
        authInfo( REPO_TEST_ADMIN_USER_AUTHINFO ).
        build();

    private final static Context CONTENT_ADMIN_CONTEXT = ContextBuilder.create().
        branch( "master" ).
        repositoryId( SystemConstants.SYSTEM_REPO.getId() ).
        authInfo( REPO_TEST_CONTENT_ADMIN_AUTHINFO ).
        build();

    private final static Context CONTENT_MANAGER_CONTEXT = ContextBuilder.create().
        branch( "master" ).
        repositoryId( SystemConstants.SYSTEM_REPO.getId() ).
        authInfo( REPO_TEST_CONTENT_MANAGER_AUTHINFO ).
        build();

    private final static Context CONTENT_CUSTOM_MANAGER_CONTEXT = ContextBuilder.create().
        branch( "master" ).
        repositoryId( SystemConstants.SYSTEM_REPO.getId() ).
        authInfo( REPO_TEST_CUSTOM_MANAGER_AUTHINFO ).
        build();

    private ProjectServiceImpl projectService;

    private SecurityServiceImpl securityService;

    @BeforeEach
    protected void setUpNode()
        throws Exception
    {
        super.setUpNode();

        final IndexServiceImpl indexService = new IndexServiceImpl();
        indexService.setIndexDataService( indexedDataService );
        indexService.setIndexServiceInternal( indexServiceInternal );

        securityService = new SecurityServiceImpl();
        securityService.setNodeService( this.nodeService );
        securityService.setIndexService( indexService );

        ADMIN_CONTEXT.runWith( () -> {
            securityService.initialize();

            final ProjectPermissionsContextManagerImpl projectAccessContextManager = new ProjectPermissionsContextManagerImpl();
            projectAccessContextManager.setRepositoryService( repositoryService );
            projectAccessContextManager.setSecurityService( securityService );

            projectService = new ProjectServiceImpl();
            projectService.setIndexService( indexService );
            projectService.setNodeService( nodeService );
            projectService.setRepositoryService( repositoryService );
            projectService.setProjectPermissionsContextManager( projectAccessContextManager );

            projectService.setSecurityService( securityService );
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
            this.branchService.get( Node.ROOT_UUID, InternalContext.create( ADMIN_CONTEXT ).repositoryId( projectRepoId ).build() );
        assertNotNull( nodeBranchEntry );

        ADMIN_CONTEXT.runWith( () -> {
            final Repository pro = repositoryService.get( projectRepoId );
            assertNotNull( pro );
        } );
    }

    @Test
    void create_with_content_admin_permissions()
    {
        final RepositoryId projectRepoId = RepositoryId.from( "com.enonic.cms.test-project" );

        final Project project = CONTENT_ADMIN_CONTEXT.
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

        CONTENT_CUSTOM_MANAGER_CONTEXT.runWith( () -> {
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

        ADMIN_CONTEXT.runWith( () -> {
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

        List.of( ContextBuilder.from( ADMIN_CONTEXT ).
            branch( ContentConstants.BRANCH_DRAFT ).
            repositoryId( projectRepoId ).
            build(), ContextBuilder.from( ADMIN_CONTEXT ).
            branch( ContentConstants.BRANCH_MASTER ).
            repositoryId( projectRepoId ).
            build() ).
            forEach( context -> context.runWith( () -> {

                final Node rootContentNode = nodeService.getByPath( ContentConstants.CONTENT_ROOT_PATH );
                final AccessControlList rootContentPermissions = rootContentNode.getPermissions();

                assertTrue( rootContentPermissions.getEntry( RoleKeys.ADMIN ).isAllowedAll() );
                assertTrue( rootContentPermissions.getEntry( RoleKeys.CONTENT_MANAGER_ADMIN ).isAllowedAll() );
                assertTrue( rootContentPermissions.getEntry( ProjectRole.OWNER.getRoleKey( projectName ) ).isAllowedAll() );
                assertTrue( rootContentPermissions.getEntry( ProjectRole.EDITOR.getRoleKey( projectName ) ).isAllowedAll() );
                assertTrue( rootContentPermissions.getEntry( ProjectRole.AUTHOR.getRoleKey( projectName ) ).isAllowed( Permission.READ,
                                                                                                                       Permission.CREATE,
                                                                                                                       Permission.MODIFY,
                                                                                                                       Permission.DELETE ) );
                assertTrue( rootContentPermissions.getEntry( ProjectRole.CONTRIBUTOR.getRoleKey( projectName ) ).
                    isAllowed( Permission.READ ) );
                assertTrue( rootContentPermissions.getEntry( ProjectRole.VIEWER.getRoleKey( projectName ) ).
                    isAllowed( Permission.READ ) );
            } ) );
    }

    @Test
    void delete()
    {
        final ProjectName projectName = ProjectName.from( "test-project" );
        doCreateProjectAsAdmin( projectName );

        ADMIN_CONTEXT.runWith( () -> {

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

        CONTENT_ADMIN_CONTEXT.runWith( () -> {
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
        ADMIN_CONTEXT.runWith( () -> assertNotNull( this.projectService.get( projectName ) ) );

        CONTENT_CUSTOM_MANAGER_CONTEXT.runWith( () -> {

            final RuntimeException ex = Assertions.assertThrows( RuntimeException.class, () -> projectService.delete( projectName ) );

            assertEquals( "Denied [user:system:custom-user] user access for [delete] operation", ex.getMessage() );
        } );
    }

    @Test
    void delete_without_permissions()
    {
        final ProjectName projectName = ProjectName.from( "test-project" );
        doCreateProjectAsAdmin( projectName );

        ADMIN_CONTEXT.runWith( () -> {
            assertNotNull( this.projectService.get( projectName ) );
        } );

        final RuntimeException ex = Assertions.assertThrows( RuntimeException.class, () -> projectService.delete( projectName ) );

        assertEquals( "Denied [user:system:test-user] user access for [delete] operation", ex.getMessage() );
    }

    @Test
    void delete_default_project_as_admin()
    {
        ADMIN_CONTEXT.runWith( () -> {
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

        ADMIN_CONTEXT.runWith( () -> {
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

        ADMIN_CONTEXT.runWith( () -> {
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

        CONTENT_ADMIN_CONTEXT.runWith( () -> {
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

        CONTENT_MANAGER_CONTEXT.runWith( () -> {
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

        CONTENT_CUSTOM_MANAGER_CONTEXT.runWith( () -> {
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

        ADMIN_CONTEXT.runWith( () -> {
            assertEquals( createdProject, projectService.get( createdProject.getName() ) );
        } );

    }

    @Test
    void get_with_admin_permissions()
    {
        final Project createdProject = doCreateProjectAsAdmin( ProjectName.from( "test-project" ) );

        CONTENT_ADMIN_CONTEXT.runWith( () -> {
            assertEquals( createdProject, projectService.get( createdProject.getName() ) );
        } );

    }

    @Test
    void get_with_manager_permissions()
    {
        final Project createdProject = doCreateProjectAsAdmin( ProjectName.from( "test-project" ) );

        CONTENT_MANAGER_CONTEXT.runWith( () -> {
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

        CONTENT_CUSTOM_MANAGER_CONTEXT.runWith( () -> {
            assertEquals( createdProject, projectService.get( createdProject.getName() ) );
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
        ADMIN_CONTEXT.runWith( () -> {

            final Project pro = projectService.get( ProjectName.from( ContentConstants.CONTENT_REPO_ID ) );
            assertEquals( ProjectConstants.DEFAULT_PROJECT, pro );
        } );

    }

    @Test
    void get_default_project()
    {
        CONTENT_CUSTOM_MANAGER_CONTEXT.runWith( () -> {
            final Project defaultProject = projectService.get( ProjectConstants.DEFAULT_PROJECT_NAME );
            assertNotNull( defaultProject );
        } );
    }

    @Test
    void modify()
    {
        doCreateProjectAsAdmin( ProjectName.from( "test-project" ) );

        ADMIN_CONTEXT.runWith( () -> {
            projectService.modify( ModifyProjectParams.create().
                name( ProjectName.from( "test-project" ) ).
                description( "new description" ).
                displayName( "new display name" ).
                icon( CreateAttachment.create().
                    mimeType( "image/png" ).
                    label( "My New Image" ).
                    name( "MyNewImage.png" ).
                    byteSource( ByteSource.wrap( "new bytes".getBytes() ) ).
                    build() ).
                build() );

            final Project modifiedProject = projectService.get( ProjectName.from( "test-project" ) );

            assertEquals( "new description", modifiedProject.getDescription() );
            assertEquals( "new display name", modifiedProject.getDisplayName() );
            assertEquals( "image/png", modifiedProject.getIcon().getMimeType() );
            assertEquals( "My New Image", modifiedProject.getIcon().getLabel() );
            assertEquals( "MyNewImage.png", modifiedProject.getIcon().getName() );
        } );

    }

    @Test
    void modify_with_deleted_role()
    {
        final RepositoryId projectRepoId = RepositoryId.from( "com.enonic.cms.test-project" );
        doCreateProjectAsAdmin( ProjectName.from( projectRepoId ) );

        ADMIN_CONTEXT.runWith( () -> {
            securityService.deletePrincipal( PrincipalKey.ofRole( "cms.project.test-project.owner" ) );

            projectService.modify( ModifyProjectParams.create().
                name( ProjectName.from( "test-project" ) ).
                description( "new description" ).
                displayName( "new display name" ).
                icon( CreateAttachment.create().
                    mimeType( "image/png" ).
                    label( "My New Image" ).
                    name( "MyNewImage.png" ).
                    byteSource( ByteSource.wrap( "new bytes".getBytes() ) ).
                    build() ).
                build() );
        } );

        assertTrue( securityService.getRole( PrincipalKey.ofRole( "cms.project.test-project.owner" ) ).isPresent() );
    }

    @Test
    void get_permissions_wrong_project()
    {
        final RuntimeException ex =
            Assertions.assertThrows( RuntimeException.class, () -> projectService.getPermissions( ProjectName.from( "test-project" ) ) );

        assertEquals( "Project [test-project] was not found", ex.getMessage() );
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

        ADMIN_CONTEXT.runWith( () -> {
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
        ADMIN_CONTEXT.runWith( () -> {
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

    private Project doCreateProjectAsAdmin( final ProjectName name )
    {
        return ADMIN_CONTEXT.callWith( () -> doCreateProject( name ) );

    }

    private Project doCreateProjectAsAdmin( final ProjectName name, final ProjectPermissions projectPermissions )
    {
        return ADMIN_CONTEXT.callWith( () -> doCreateProject( name, projectPermissions ) );
    }

    private Project doCreateProject( final ProjectName name )
    {
        return doCreateProject( name, ProjectPermissions.create().
            addOwner( REPO_TEST_OWNER.getKey() ).addViewer( "user:system:custom1" ).addViewer( "user:system:custom2" ).
            build() );
    }

    private Project doCreateProject( final ProjectName name, final ProjectPermissions projectPermissions )
    {
        final Project project = this.projectService.create( CreateProjectParams.create().
            name( name ).
            description( "description" ).
            displayName( "Project display name" ).
            icon( CreateAttachment.create().
                mimeType( "image/jpg" ).
                label( "My Image 1" ).
                name( "MyImage.jpg" ).
                byteSource( ByteSource.wrap( "bytes".getBytes() ) ).
                build() ).
            build() );

        if ( projectPermissions != null )
        {
            this.projectService.modifyPermissions( name, projectPermissions );
        }

        return project;
    }

}
