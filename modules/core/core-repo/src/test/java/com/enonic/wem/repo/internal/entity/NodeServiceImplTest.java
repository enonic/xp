package com.enonic.wem.repo.internal.entity;

import java.io.IOException;
import java.time.Instant;

import org.junit.Before;
import org.junit.Test;

import com.google.common.io.ByteSource;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.CreateRootNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RenameNodeParams;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.snapshot.RestoreParams;
import com.enonic.xp.snapshot.RestoreResult;
import com.enonic.xp.snapshot.SnapshotParams;
import com.enonic.xp.snapshot.SnapshotResult;
import com.enonic.xp.util.BinaryReference;

import static org.junit.Assert.*;

public class NodeServiceImplTest
    extends AbstractNodeTest
{
    private NodeServiceImpl nodeService;

    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
        this.nodeService = new NodeServiceImpl();
        this.nodeService.setIndexServiceInternal( indexServiceInternal );
        this.nodeService.setQueryService( queryService );
        this.nodeService.setNodeDao( nodeDao );
        this.nodeService.setBranchService( branchService );
        this.nodeService.setSnapshotService( this.snapshotService );
        this.nodeService.setStorageService( this.storageService );
        this.nodeService.setSearchService( this.searchService );

        this.createDefaultRootNode();
    }

    @Test
    public void get_by_id()
        throws Exception
    {
        final Node createdNode = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            build() );

        final Node fetchedNode = this.nodeService.getById( NodeId.from( createdNode.id() ) );

        assertEquals( createdNode, fetchedNode );
    }

    @Test(expected = NodeNotFoundException.class)
    public void get_by_id_non_existing()
        throws Exception
    {
        this.nodeService.getById( NodeId.from( "a" ) );
    }

    @Test
    public void createRootNode()
    {
        final User user = User.create().
            key( PrincipalKey.ofUser( UserStoreKey.system(), "user1" ) ).
            displayName( "User 1" ).
            modifiedTime( Instant.now() ).
            email( "user1@enonic.com" ).
            login( "user1" ).
            build();

        this.nodeService.createRootNode( CreateRootNodeParams.create().
            childOrder( ChildOrder.from( "_name ASC" ) ).
            permissions( AccessControlList.of( AccessControlEntry.create().
                allowAll().
                principal( user.getKey() ).
                build() ) ).
            build() );

        printContentRepoIndex();

        final Context context = ContextBuilder.create().
            authInfo( AuthenticationInfo.create().
                user( user ).
                principals( RoleKeys.CONTENT_MANAGER_ADMIN ).
                build() ).
            branch( WS_DEFAULT ).
            repositoryId( TEST_REPO.getId() ).
            build();

        context.runWith( () -> assertNotNull( this.nodeService.getByPath( NodePath.ROOT ) ) );
        context.runWith( () -> assertNotNull( this.nodeService.getRoot() ) );
    }

    @Test
    public void rename()
        throws Exception
    {
        final Node createdNode = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            build() );

        nodeService.rename( RenameNodeParams.create().
            nodeName( NodeName.from( "my-node-edited" ) ).
            nodeId( createdNode.id() ).
            build() );

        final Node renamedNode = nodeService.getById( createdNode.id() );

        assertEquals( "my-node-edited", renamedNode.name().toString() );
    }

    @Test
    public void create()
        throws Exception
    {

        final ChildOrder childOrder = ChildOrder.create().
            add( FieldOrderExpr.create( NodeIndexPath.TIMESTAMP, OrderExpr.Direction.DESC ) ).
            add( FieldOrderExpr.create( NodeIndexPath.NAME, OrderExpr.Direction.ASC ) ).
            build();

        final AccessControlList aclList = AccessControlList.create().
            add( AccessControlEntry.create().
                principal( PrincipalKey.from( "user:myuserstore:rmy" ) ).
                allow( Permission.READ ).
                build() ).
            build();

        final CreateNodeParams params = CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            permissions( aclList ).
            childOrder( childOrder ).
            build();

        final Node node = this.nodeService.create( params );

        refresh();

        assertTrue( node.getPermissions() != null );
        assertEquals( aclList, node.getPermissions() );
        assertEquals( childOrder, node.getChildOrder() );
    }

    @Test
    public void snapshot_restore()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "myNode" ).
            build() );

        final SnapshotResult result = this.nodeService.snapshot( SnapshotParams.create().
            snapshotName( "my-snapshot" ).
            repositoryId( CTX_DEFAULT.getRepositoryId() ).
            build() );

        assertEquals( SnapshotResult.State.SUCCESS, result.getState() );

        doDeleteNode( node.id() );

        assertNull( getNodeById( node.id() ) );

        this.nodeService.restore( RestoreParams.create().
            snapshotName( "my-snapshot" ).
            repositoryId( CTX_DEFAULT.getRepositoryId() ).
            build() );

        assertNotNull( getNodeById( node.id() ) );
    }

    @Test
    public void non_existing_snapshot()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "myNode" ).
            build() );

        assertNotNull( getNodeById( node.id() ) );

        final RestoreResult result = this.nodeService.restore( RestoreParams.create().
            snapshotName( "my-snapshot" ).
            repositoryId( CTX_DEFAULT.getRepositoryId() ).
            build() );

        assertTrue( result.isFailed() );

        assertNotNull( getNodeById( node.id() ) );
    }

    @Test
    public void test_get_binary_key()
    {

        final PropertyTree data = new PropertyTree();
        final BinaryReference binaryRef1 = BinaryReference.from( "binary" );
        data.addBinaryReference( "my-binary-1", binaryRef1 );

        final String binarySource = "binary_source";

        final Node node = this.nodeService.create( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            data( data ).
            attachBinary( binaryRef1, ByteSource.wrap( binarySource.getBytes() ) ).
            build() );

        final String key = this.nodeService.getBinaryKey( node.id(), binaryRef1 );

        assertNotNull( key );
    }

    @Test
    public void test_get_binary()
        throws IOException
    {

        final PropertyTree data = new PropertyTree();
        final BinaryReference binaryRef1 = BinaryReference.from( "binary" );
        data.addBinaryReference( "my-binary-1", binaryRef1 );

        final String binarySource = "binary_source";

        final Node node = this.nodeService.create( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            data( data ).
            attachBinary( binaryRef1, ByteSource.wrap( binarySource.getBytes() ) ).
            build() );

        final ByteSource source = this.nodeService.getBinary( node.id(), binaryRef1 );

        assertArrayEquals( source.read(), binarySource.getBytes() );
    }

}
