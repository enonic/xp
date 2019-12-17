package com.enonic.xp.repo.impl.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.node.AbstractNodeTest;
import com.enonic.xp.repo.impl.node.NodeHelper;
import com.enonic.xp.repo.impl.node.NodeServiceImpl;
import com.enonic.xp.repository.CreateBranchParams;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.DeleteBranchParams;
import com.enonic.xp.repository.DeleteRepositoryParams;
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

public class RepositoryServiceImplTest
    extends AbstractNodeTest
{

    private NodeServiceImpl nodeService;

    @BeforeEach
    public void setUp()
        throws Exception
    {
        this.nodeService = new NodeServiceImpl();
        this.nodeService.setIndexServiceInternal( this.indexServiceInternal );
        this.nodeService.setBinaryService( this.binaryService );
        this.nodeService.setNodeSearchService( this.searchService );
        this.nodeService.setNodeStorageService( this.storageService );
        this.nodeService.setRepositoryService( this.repositoryService );

    }

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
}
