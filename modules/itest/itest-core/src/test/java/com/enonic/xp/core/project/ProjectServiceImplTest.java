package com.enonic.xp.core.project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.io.ByteSource;

import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.impl.project.ProjectServiceImpl;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.project.CreateProjectParams;
import com.enonic.xp.project.ModifyProjectParams;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.index.IndexServiceImpl;
import com.enonic.xp.repo.impl.node.AbstractNodeTest;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.security.User;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ProjectServiceImplTest
    extends AbstractNodeTest
{
    private static final User REPO_TEST_DEFAULT_USER =
        User.create().key( PrincipalKey.ofUser( IdProviderKey.system(), "repo-test-user" ) ).login( "repo-test-user" ).build();

    private static final AuthenticationInfo REPO_TEST_DEFAULT_USER_AUTHINFO = AuthenticationInfo.create().
        principals( RoleKeys.AUTHENTICATED ).
        principals( RoleKeys.ADMIN ).
        user( REPO_TEST_DEFAULT_USER ).
        build();

    private final static Context ADMIN_CONTEXT = ContextBuilder.create().
        branch( "master" ).
        repositoryId( SystemConstants.SYSTEM_REPO.getId() ).
        authInfo( REPO_TEST_DEFAULT_USER_AUTHINFO ).
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

        projectService = new ProjectServiceImpl();
        projectService.setIndexService( indexService );
        projectService.setNodeService( nodeService );
        projectService.setRepositoryService( repositoryService );
    }

    @Test
    void create()
    {
        final RepositoryId projectRepoId = RepositoryId.from( "com.enonic.cms.test-project" );

        final Project project = doCreateProject( ProjectName.from( projectRepoId ) );
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
    void delete()
    {
        final ProjectName projectName = ProjectName.from( "test-project" );
        doCreateProject( projectName );

        ADMIN_CONTEXT.runWith( () -> {

            assertNotNull( this.repositoryService.get( projectName.getRepoId() ) );

            this.projectService.delete( projectName );

            assertNull( this.repositoryService.get( projectName.getRepoId() ) );

        } );
    }

    @Test
    void list()
    {
        doCreateProject( ProjectName.from( "test-project1" ) );
        doCreateProject( ProjectName.from( "test-project2" ) );
        doCreateProject( ProjectName.from( "test-project3" ) );

        ADMIN_CONTEXT.runWith( () -> {
            assertEquals( 3, projectService.list().getSize() );

            this.projectService.delete( ProjectName.from( "test-project2" ) );
            assertEquals( 2, projectService.list().getSize() );
        } );

    }

    @Test
    void get()
    {
        final Project createdProject = doCreateProject( ProjectName.from( "test-project" ) );

        ADMIN_CONTEXT.runWith( () -> {
            assertEquals( createdProject, projectService.get( ProjectName.from( "test-project" ) ) );
        } );

    }

    @Test
    void modify()
    {
        doCreateProject( ProjectName.from( "test-project" ) );

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

    private Project doCreateProject( final ProjectName name )
    {
        return ADMIN_CONTEXT.callWith( () -> this.projectService.create( CreateProjectParams.create().
            name( name ).
            description( "description" ).
            displayName( "Project display name" ).
            icon( CreateAttachment.create().
                mimeType( "image/jpg" ).
                label( "My Image 1" ).
                name( "MyImage.jpg" ).
                byteSource( ByteSource.wrap( "bytes".getBytes() ) ).
                build() ).
            build() ) );

    }

    private Repository doCreateRepo( final String id )
    {
        return ADMIN_CONTEXT.callWith( () -> this.repositoryService.createRepository( CreateRepositoryParams.create().
            repositoryId( RepositoryId.from( id ) ).
            rootPermissions( AccessControlList.create().
                add( AccessControlEntry.create().
                    principal( TEST_DEFAULT_USER.getKey() ).
                    allowAll().
                    build() ).
                build() ).
            build() ) );
    }

    private Repository getPersistedRepoWithoutCache( String id )
    {
        return ADMIN_CONTEXT.callWith( () -> {
            repositoryService.invalidateAll();
            return this.repositoryService.get( RepositoryId.from( id ) );
        } );
    }
}
