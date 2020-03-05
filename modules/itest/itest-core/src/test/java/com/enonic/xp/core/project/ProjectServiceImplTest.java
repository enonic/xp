package com.enonic.xp.core.project;

import java.util.List;
import java.util.Locale;

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
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.project.CreateProjectParams;
import com.enonic.xp.project.ModifyProjectParams;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectPermissions;
import com.enonic.xp.project.Projects;
import com.enonic.xp.project.layer.ContentLayer;
import com.enonic.xp.project.layer.ContentLayerKey;
import com.enonic.xp.project.layer.ContentLayerName;
import com.enonic.xp.project.layer.CreateLayerParams;
import com.enonic.xp.project.layer.ModifyLayerParams;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.index.IndexServiceImpl;
import com.enonic.xp.repo.impl.node.AbstractNodeTest;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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

    @BeforeEach
    protected void setUpNode()
        throws Exception
    {
        super.setUpNode();

        final IndexServiceImpl indexService = new IndexServiceImpl();
        indexService.setIndexDataService( indexedDataService );
        indexService.setIndexServiceInternal( indexServiceInternal );

        final ProjectPermissionsContextManagerImpl projectAccessContextManager = new ProjectPermissionsContextManagerImpl();
        projectAccessContextManager.setRepositoryService( repositoryService );

        projectService = new ProjectServiceImpl();
        projectService.setIndexService( indexService );
        projectService.setNodeService( nodeService );
        projectService.setRepositoryService( repositoryService );
        projectService.setProjectPermissionsContextManager( projectAccessContextManager );
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
    void create_layer()
    {
        final RepositoryId projectRepoId = RepositoryId.from( "com.enonic.cms.test-project" );

        final Project createdProject = doCreateProjectAsAdmin( ProjectName.from( projectRepoId ) );

        final ContentLayerKey contentLayerKey = ContentLayerKey.from( createdProject.getName(), ContentLayerName.DEFAULT_LAYER_NAME );
        final ContentLayer createdLayer = doCreateLayerAsAdmin( contentLayerKey );

        ADMIN_CONTEXT.runWith( () -> {
            final Repository fetchedRepo = repositoryService.get( projectRepoId );
            final Project fetchedProject = Project.from( fetchedRepo );

            assertNotEquals( fetchedProject, createdProject );
            assertEquals( fetchedProject.getLayers().getLayer( contentLayerKey ), createdLayer );
        } );
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
    void delete_layer()
    {
        final ProjectName projectName = ProjectName.from( "test-project" );
        doCreateProjectAsAdmin( projectName );

        final ContentLayerKey contentLayerKey = ContentLayerKey.from( projectName, ContentLayerName.DEFAULT_LAYER_NAME );
        doCreateLayerAsAdmin( contentLayerKey );

        ADMIN_CONTEXT.runWith( () -> {

            assertTrue( this.projectService.deleteLayer( contentLayerKey ) );

            final Project project = this.projectService.get( projectName );
            final ContentLayer layer = project.getLayers().getLayer( contentLayerKey );
            assertNull( layer );

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
                                ProjectPermissions.create().addExpert( REPO_TEST_OWNER.getKey() ).build() );
        doCreateProjectAsAdmin( ProjectName.from( "test-project4" ),
                                ProjectPermissions.create().addContributor( REPO_TEST_OWNER.getKey() ).build() );

        CONTENT_CUSTOM_MANAGER_CONTEXT.runWith( () -> {
            final Projects projects = projectService.list();

            assertEquals( 4, projectService.list().getSize() );
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

        final RuntimeException ex = Assertions.assertThrows( RuntimeException.class, () -> projectService.list() );

        assertEquals( "Denied [user:system:test-user] user access for [list] operation", ex.getMessage() );
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
                permissions( ProjectPermissions.create().
                    addOwner( PrincipalKey.from( "user:store:new" ) ).
                    build() ).
                build() );

            final Project modifiedProject = projectService.get( ProjectName.from( "test-project" ) );

            assertEquals( "new description", modifiedProject.getDescription() );
            assertEquals( "new display name", modifiedProject.getDisplayName() );
            assertEquals( "image/png", modifiedProject.getIcon().getMimeType() );
            assertEquals( "My New Image", modifiedProject.getIcon().getLabel() );
            assertEquals( "MyNewImage.png", modifiedProject.getIcon().getName() );
            assertTrue( modifiedProject.getPermissions().getOwner().contains( PrincipalKey.from( "user:store:new" ) ) );
        } );

    }

    @Test
    void modify_layer()
    {
        final ProjectName projectName = ProjectName.from( "test-project" );
        doCreateProjectAsAdmin( projectName );

        final ContentLayerKey contentLayerKey = ContentLayerKey.from( projectName, ContentLayerName.DEFAULT_LAYER_NAME );
        final ContentLayerKey contentLayerKey2 = ContentLayerKey.from( projectName, ContentLayerName.from( "layer2" ) );
        final ContentLayerKey contentLayerKey3 = ContentLayerKey.from( projectName, ContentLayerName.from( "layer3" ) );

        final ContentLayer contentLayer = doCreateLayerAsAdmin( contentLayerKey );
        final ContentLayer contentLayer2 = doCreateLayerAsAdmin( contentLayerKey2 );
        final ContentLayer contentLayer3 = doCreateLayerAsAdmin( contentLayerKey3 );

        ADMIN_CONTEXT.runWith( () -> {
            projectService.modifyLayer( ModifyLayerParams.create().
                key( contentLayerKey ).
                description( "new description" ).
                displayName( "new display name" ).
                locale( Locale.US ).
                parentKeys( List.of( ContentLayerKey.from( "default:newParent" ) ) ).
                icon( CreateAttachment.create().
                    mimeType( "image/png" ).
                    label( "My New Image" ).
                    name( "MyNewImage.png" ).
                    byteSource( ByteSource.wrap( "new bytes".getBytes() ) ).
                    build() ).
                build() );

            final Project modifiedProject = projectService.get( ProjectName.from( "test-project" ) );

            assertEquals( 3, modifiedProject.getLayers().getSize() );
            assertEquals( contentLayer2, modifiedProject.getLayers().getLayer( contentLayerKey2 ) );
            assertEquals( contentLayer3, modifiedProject.getLayers().getLayer( contentLayerKey3 ) );
            assertNotEquals( contentLayer, modifiedProject.getLayers().getLayer( contentLayerKey ) );

            final ContentLayer modifiedLayer = modifiedProject.getLayers().getLayer( contentLayerKey );

            assertEquals( "new description", modifiedLayer.getDescription() );
            assertEquals( "new display name", modifiedLayer.getDisplayName() );
            assertEquals( Locale.US, modifiedLayer.getLocale() );
            assertEquals( List.of( ContentLayerKey.from( "default:newParent" ) ), modifiedLayer.getParentKeys() );
            assertEquals( "image/png", modifiedLayer.getIcon().getMimeType() );
            assertEquals( "My New Image", modifiedLayer.getIcon().getLabel() );
            assertEquals( "MyNewImage.png", modifiedLayer.getIcon().getName() );
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

    private ContentLayer doCreateLayerAsAdmin( final ContentLayerKey key )
    {
        return ADMIN_CONTEXT.callWith( () -> doCreateLayer( key ) );
    }

    private Project doCreateProject( final ProjectName name )
    {
        return doCreateProject( name, ProjectPermissions.create().
            addOwner( REPO_TEST_OWNER.getKey() ).
            build() );
    }

    private Project doCreateProject( final ProjectName name, final ProjectPermissions projectPermissions )
    {
        return this.projectService.create( CreateProjectParams.create().
            name( name ).
            description( "description" ).
            displayName( "Project display name" ).
            icon( CreateAttachment.create().
                mimeType( "image/jpg" ).
                label( "My Image 1" ).
                name( "MyImage.jpg" ).
                byteSource( ByteSource.wrap( "bytes".getBytes() ) ).
                build() ).
            permissions( projectPermissions ).
            build() );

    }

    private ContentLayer doCreateLayer( final ContentLayerKey key )
    {
        return this.projectService.createLayer( CreateLayerParams.create().
            key( key ).
            description( "Layer description" ).
            displayName( "Layer display name" ).
            locale( Locale.ENGLISH ).
            addParentKeys( List.of( ContentLayerKey.from( "default:base" ) ) ).
            icon( CreateAttachment.create().
                mimeType( "image/jpg" ).
                label( "My Layer Image" ).
                name( "MyLayerImage.jpg" ).
                byteSource( ByteSource.wrap( "layer bytes".getBytes() ) ).
                build() ).
            build() );
    }

}
