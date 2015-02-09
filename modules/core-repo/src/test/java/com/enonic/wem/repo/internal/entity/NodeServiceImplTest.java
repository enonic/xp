package com.enonic.wem.repo.internal.entity;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.context.ContextBuilder;
import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.CreateRootNodeParams;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodeIndexPath;
import com.enonic.wem.api.node.NodeName;
import com.enonic.wem.api.node.NodeNotFoundException;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.RenameNodeParams;
import com.enonic.wem.api.query.expr.FieldOrderExpr;
import com.enonic.wem.api.query.expr.OrderExpr;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.RoleKeys;
import com.enonic.wem.api.security.User;
import com.enonic.wem.api.security.UserStoreKey;
import com.enonic.wem.api.security.acl.AccessControlEntry;
import com.enonic.wem.api.security.acl.AccessControlList;
import com.enonic.wem.api.security.acl.Permission;
import com.enonic.wem.api.security.auth.AuthenticationInfo;
import com.enonic.wem.api.snapshot.RestoreParams;
import com.enonic.wem.api.snapshot.SnapshotParams;
import com.enonic.wem.api.snapshot.SnapshotResult;

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
        this.nodeService.setIndexService( indexService );
        this.nodeService.setQueryService( queryService );
        this.nodeService.setNodeDao( nodeDao );
        this.nodeService.setVersionService( versionService );
        this.nodeService.setBranchService( branchService );
        this.nodeService.setSnapshotService( this.snapshotService );
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
            build() );

        assertEquals( SnapshotResult.State.SUCCESS, result.getState() );

        doDeleteNode( node.id() );

        assertNull( getNodeById( node.id() ) );

        this.nodeService.restore( RestoreParams.create().snapshotName( "my-snapshot" ).
            build() );

        assertNotNull( getNodeById( node.id() ) );
    }
}
