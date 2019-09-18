package com.enonic.xp.repo.impl.repository;

import org.junit.jupiter.api.Test;

import com.google.common.io.ByteSource;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.BinaryAttachment;
import com.enonic.xp.node.BinaryAttachments;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.node.AbstractNodeTest;
import com.enonic.xp.repo.impl.node.NodeHelper;
import com.enonic.xp.repository.CreateBranchParams;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.DeleteBranchParams;
import com.enonic.xp.repository.DeleteRepositoryParams;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryData;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.UpdateRepositoryParams;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.security.User;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.util.BinaryReference;

import static org.junit.jupiter.api.Assertions.*;

public class RepositoryServiceImplTest
    extends AbstractNodeTest
{

    public static final User REPO_TEST_DEFAULT_USER =
        User.create().key( PrincipalKey.ofUser( IdProviderKey.system(), "repo-test-user" ) ).login( "repo-test-user" ).build();

    public static final AuthenticationInfo REPO_TEST_DEFAULT_USER_AUTHINFO = AuthenticationInfo.create().
        principals( RoleKeys.AUTHENTICATED ).
        principals( RoleKeys.ADMIN ).
        user( REPO_TEST_DEFAULT_USER ).
        build();

    private final static Context ADMIN_CONTEXT = ContextBuilder.create().
        branch( "master" ).
        repositoryId( SystemConstants.SYSTEM_REPO.getId() ).
        authInfo( REPO_TEST_DEFAULT_USER_AUTHINFO ).
        build();

    @Test
    public void create()
        throws Exception
    {
        final Repository repo = doCreateRepo( "fisk" );
        assertNotNull( repo );
        assertEquals( RepositoryId.from( "fisk" ), repo.getId() );
    }

    @Test
    public void update_data()
        throws Exception
    {
        final Repository repo = doCreateRepo( "fisk" );

        Context mockCurrentContext = ContextBuilder.create().
            branch( "master" ).
            repositoryId( "fisk" ).
            authInfo( REPO_TEST_DEFAULT_USER_AUTHINFO ).
            build();

        PropertyTree data = new PropertyTree();
        data.setString( "myProp", "b" );

        mockCurrentContext.callWith( () -> repositoryService.updateRepository( UpdateRepositoryParams.create().
            repositoryId( RepositoryId.from( "fisk" ) ).
            data( RepositoryData.from( data ) ).
            build() ) );

        final Repository persistedRepo = getPersistedRepoWithoutCache( "fisk" );

        assertEquals( "b", persistedRepo.getData().getValue().getString( "myProp" ) );
    }

    @Test
    public void update_attachment()
        throws Exception
    {
        final Repository repo = doCreateRepo( "fisk" );

        final BinaryReference binaryRef = BinaryReference.from( "image1.jpg" );
        ByteSource binarySource = ByteSource.wrap( "this-is-the-binary-data-for-image1".getBytes() );

        Context mockCurrentContext = ContextBuilder.create().
            branch( "master" ).
            repositoryId( "fisk" ).
            authInfo( REPO_TEST_DEFAULT_USER_AUTHINFO ).
            build();

        PropertyTree data = new PropertyTree();
        data.setBinaryReference( "someIcon", binaryRef );

        mockCurrentContext.runWith( () -> {
            repositoryService.updateRepository( UpdateRepositoryParams.create().
                repositoryId( RepositoryId.from( "fisk" ) ).
                data( RepositoryData.from( data ) ).
                attachments( BinaryAttachments.create().
                    add( new BinaryAttachment( binaryRef, binarySource ) ).
                    build() ).
                build() );
        } );

        final Repository persistedRepo = getPersistedRepoWithoutCache( "fisk" );

        AttachedBinary attachedBinary =
            persistedRepo.getAttachments().getAttachedBinaries().getByBinaryReference( BinaryReference.from( "image1.jpg" ) );

        ByteSource persistedAttachment = binaryService.get( SystemConstants.SYSTEM_REPO_ID, attachedBinary );

        assertTrue( binarySource.contentEquals( persistedAttachment ) );
    }

    @Test
    public void create_default_acl()
        throws Exception
    {
        final Repository repo = doCreateRepo( "fisk" );
        assertNotNull( repo );
        assertEquals( RepositoryId.from( "fisk" ), repo.getId() );

        final Node rootNode = ADMIN_CONTEXT.callWith( () -> this.nodeService.getRoot() );
        final AccessControlList acl = rootNode.getPermissions();

        System.out.println( acl.toString() );
    }

    @Test
    public void get()
        throws Exception
    {
        final Repository repo = doCreateRepo( "fisk" );

        final Repository persistedRepo = ADMIN_CONTEXT.callWith( () -> this.repositoryService.get( repo.getId() ) );
        assertNotNull( persistedRepo );
    }

    @Test
    public void delete_branch()
        throws Exception
    {
        final Node myNode = createNode( NodePath.ROOT, "myNode" );
        NodeHelper.runAsAdmin( () -> this.repositoryService.deleteBranch( DeleteBranchParams.from( CTX_DEFAULT.getBranch() ) ) );
        NodeHelper.runAsAdmin( () -> this.repositoryService.createBranch( CreateBranchParams.from( CTX_DEFAULT.getBranch().toString() ) ) );
        assertNull( getNode( myNode.id() ) );
    }

    @Test
    public void create_branch_creates_in_repo()
        throws Exception
    {
        doCreateRepo( "fisk" );

        Branch branch = Branch.from( "myBranch" );

        Context mockCurrentContext = ContextBuilder.create().
            branch( "master" ).
            repositoryId( "fisk" ).
            authInfo( REPO_TEST_DEFAULT_USER_AUTHINFO ).
            build();

        mockCurrentContext.callWith( () -> repositoryService.createBranch( CreateBranchParams.from( branch ) ) );

        final Repository persistedRepo = getPersistedRepoWithoutCache( "fisk" );
        assertTrue( persistedRepo.getBranches().contains( branch ) );
    }


    @Test
    public void delete_branch_deletes_from_repo()
        throws Exception
    {
        final Repository repo = doCreateRepo( "fisk" );

        Context mockCurrentContext = ContextBuilder.create().
            branch( "master" ).
            repositoryId( "fisk" ).
            authInfo( REPO_TEST_DEFAULT_USER_AUTHINFO ).
            build();

        Branch branch = Branch.from( "myBranch" );
        NodeHelper.runAsAdmin( () -> repositoryService.createBranch( CreateBranchParams.from( branch ) ) );
        NodeHelper.runAsAdmin( () -> repositoryService.deleteBranch( DeleteBranchParams.from( branch ) ) );

        final Repository persistedRepo = getPersistedRepoWithoutCache( "fisk" );
        assertFalse( persistedRepo.getBranches().contains( branch ) );
    }

    @Test
    public void deleting_repo_invalidates_path_cache()
        throws Exception
    {
        final Repository repo = doCreateRepo( "fisk" );

        ContextBuilder.from( ContextAccessor.current() ).
            repositoryId( repo.getId() ).
            branch( Branch.from( "master" ) ).
            build().
            runWith( () -> createNode( NodePath.ROOT, "fisk" ) );

        ADMIN_CONTEXT.callWith( () -> this.repositoryService.deleteRepository( DeleteRepositoryParams.from( repo.getId() ) ) );

        final Repository repoRecreated = doCreateRepo( "fisk" );

        ContextBuilder.from( ContextAccessor.current() ).
            repositoryId( repoRecreated.getId() ).
            branch( Branch.from( "master" ) ).
            build().
            runWith( () -> createNode( NodePath.ROOT, "fisk" ) );
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
