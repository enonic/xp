package com.enonic.wem.itests.core.entity;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.api.query.expr.FieldOrderExpr;
import com.enonic.wem.api.query.expr.OrderExpr;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.acl.AccessControlEntry;
import com.enonic.wem.api.security.acl.AccessControlList;
import com.enonic.wem.api.security.acl.Permission;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodeName;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.core.entity.NodeServiceImpl;
import com.enonic.wem.api.node.RenameNodeParams;
import com.enonic.wem.api.node.NodeNotFoundException;

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
        this.nodeService.setWorkspaceService( workspaceService );
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
            add( FieldOrderExpr.create( "modifiedTime", OrderExpr.Direction.DESC ) ).
            add( FieldOrderExpr.create( "name", OrderExpr.Direction.ASC ) ).
            build();

        final AccessControlList aclList = AccessControlList.create().
            add( AccessControlEntry.create().
                principal( PrincipalKey.from( "myuserstore:user:rmy" ) ).
                allow( Permission.READ ).
                build() ).
            build();

        final CreateNodeParams params = CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            accessControlList( aclList ).
            childOrder( childOrder ).
            build();

        final Node node = this.nodeService.create( params );

        refresh();

        assertTrue( node.getAccessControlList() != null );
        assertEquals( aclList, node.getAccessControlList() );
        assertEquals( childOrder, node.getChildOrder() );
    }

}
