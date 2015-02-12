package com.enonic.wem.repo.internal.entity;

import org.junit.Test;

import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.FindNodeVersionsResult;
import com.enonic.wem.api.node.MoveNodeException;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodeName;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.security.acl.AccessControlEntry;
import com.enonic.wem.api.security.acl.AccessControlList;

import static org.junit.Assert.*;

public class MoveNodeCommandTest
    extends AbstractNodeTest
{

    @Test
    public void new_name_only()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "mynode" ).
            parent( NodePath.ROOT ).
            setNodeId( NodeId.from( "mynode" ) ).
            build() );

        MoveNodeCommand.create().
            queryService( this.queryService ).
            indexServiceInternal( this.indexServiceInternal ).
            branchService( this.branchService ).
            nodeDao( this.nodeDao ).
            versionService( this.versionService ).
            id( node.id() ).
            newNodeName( NodeName.from( "mynode2" ) ).
            newParent( node.parentPath() ).
            build().
            execute();

        final Node movedNode = getNodeById( node.id() );

        assertEquals( NodePath.ROOT, movedNode.parentPath() );
        assertEquals( "mynode2", movedNode.name().toString() );
    }


    @Test(expected = MoveNodeException.class)
    public void move_to_child_as_self_not_allowed()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "mynode" ).
            parent( NodePath.ROOT ).
            setNodeId( NodeId.from( "mynode" ) ).
            build() );

        MoveNodeCommand.create().
            queryService( this.queryService ).
            indexServiceInternal( this.indexServiceInternal ).
            branchService( this.branchService ).
            nodeDao( this.nodeDao ).
            versionService( this.versionService ).
            id( node.id() ).
            newNodeName( NodeName.from( "mynode2" ) ).
            newParent( node.path() ).
            build().
            execute();
    }

    @Test(expected = MoveNodeException.class)
    public void move_to_child_of_own_child_not_allowed()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "mynode" ).
            parent( NodePath.ROOT ).
            setNodeId( NodeId.from( "mynode" ) ).
            build() );

        final Node child = createNode( CreateNodeParams.create().
            name( "child" ).
            parent( node.path() ).
            setNodeId( NodeId.from( "child" ) ).
            build() );

        MoveNodeCommand.create().
            queryService( this.queryService ).
            indexServiceInternal( this.indexServiceInternal ).
            branchService( this.branchService ).
            nodeDao( this.nodeDao ).
            versionService( this.versionService ).
            id( node.id() ).
            newNodeName( NodeName.from( "mynode2" ) ).
            newParent( child.path() ).
            build().
            execute();
    }


    @Test(expected = MoveNodeException.class)
    public void move_node_already_exists()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "mynode" ).
            parent( NodePath.ROOT ).
            setNodeId( NodeId.from( "mynode" ) ).
            build() );

        final Node newParent = createNode( CreateNodeParams.create().
            name( "new-parent" ).
            parent( NodePath.ROOT ).
            setNodeId( NodeId.from( "newparent" ) ).
            build() );

        createNode( CreateNodeParams.create().
            name( "mynode" ).
            parent( newParent.path() ).
            build() );

        MoveNodeCommand.create().
            queryService( this.queryService ).
            indexServiceInternal( this.indexServiceInternal ).
            branchService( this.branchService ).
            nodeDao( this.nodeDao ).
            versionService( this.versionService ).
            id( node.id() ).
            newNodeName( NodeName.from( "mynode" ) ).
            newParent( newParent.path() ).
            build().
            execute();
    }

    @Test
    public void move_node_already_exists_force()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "mynode" ).
            parent( NodePath.ROOT ).
            setNodeId( NodeId.from( "mynode" ) ).
            build() );

        final Node newParent = createNode( CreateNodeParams.create().
            name( "new-parent" ).
            parent( NodePath.ROOT ).
            setNodeId( NodeId.from( "newparent" ) ).
            build() );

        final Node equalNode = createNode( CreateNodeParams.create().
            name( "mynode" ).
            parent( newParent.path() ).
            build() );

        MoveNodeCommand.create().
            queryService( this.queryService ).
            indexServiceInternal( this.indexServiceInternal ).
            branchService( this.branchService ).
            nodeDao( this.nodeDao ).
            versionService( this.versionService ).
            id( node.id() ).
            newNodeName( NodeName.from( "mynode" ) ).
            newParent( newParent.path() ).
            overwriteExisting( true ).
            build().
            execute();

        assertNotNull( getNodeById( node.id() ) );
        assertEquals( "mynode-copy", getNodeById( node.id() ).name().toString() );
        assertNotNull( getNodeById( equalNode.id() ) );
    }


    @Test
    public void move_to_new_parent()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "mynode" ).
            parent( NodePath.ROOT ).
            setNodeId( NodeId.from( "mynode" ) ).
            build() );

        final Node newParent = createNode( CreateNodeParams.create().
            name( "new-parent" ).
            parent( NodePath.ROOT ).
            setNodeId( NodeId.from( "newparent" ) ).
            build() );

        MoveNodeCommand.create().
            queryService( this.queryService ).
            indexServiceInternal( this.indexServiceInternal ).
            branchService( this.branchService ).
            nodeDao( this.nodeDao ).
            versionService( this.versionService ).
            id( node.id() ).
            newParent( newParent.path() ).
            build().
            execute();

        final Node movedNode = getNodeById( node.id() );

        assertEquals( newParent.path(), movedNode.parentPath() );
        assertEquals( "mynode", movedNode.name().toString() );
    }

    @Test
    public void move_with_children()
        throws Exception
    {
        final Node parentNode = createNode( CreateNodeParams.create().
            name( "myparent" ).
            parent( NodePath.ROOT ).
            setNodeId( NodeId.from( "myparent" ) ).
            permissions( AccessControlList.of( AccessControlEntry.create().principal( TEST_DEFAULT_USER.getKey() ).allowAll().build() ) ).
            build() );

        final Node node = createNode( CreateNodeParams.create().
            name( "mynode" ).
            parent( parentNode.path() ).
            setNodeId( NodeId.from( "mynode" ) ).
            inheritPermissions( true ).
            build() );

        final Node child1 = createNode( CreateNodeParams.create().
            name( "child1" ).
            parent( node.path() ).
            setNodeId( NodeId.from( "child1" ) ).
            build() );

        final Node child2 = createNode( CreateNodeParams.create().
            name( "child2" ).
            parent( node.path() ).
            setNodeId( NodeId.from( "child2" ) ).
            build() );

        final Node child1_1 = createNode( CreateNodeParams.create().
            name( "child1_1" ).
            parent( child1.path() ).
            setNodeId( NodeId.from( "child1_1" ) ).
            build() );

        final Node newParent = createNode( CreateNodeParams.create().
            name( "new-parent" ).
            parent( NodePath.ROOT ).
            setNodeId( NodeId.from( "newparent" ) ).
            build() );

        assertEquals( 1, getVersions( node ).getHits() );
        assertEquals( 1, getVersions( child1 ).getHits() );
        assertEquals( 1, getVersions( child2 ).getHits() );

        assertNotNull( getNodeByPath( NodePath.newNodePath( node.path(), child1.name().toString() ).build() ) );

        final Node movedNode = MoveNodeCommand.create().
            queryService( this.queryService ).
            indexServiceInternal( this.indexServiceInternal ).
            branchService( this.branchService ).
            nodeDao( this.nodeDao ).
            versionService( this.versionService ).
            id( node.id() ).
            newParent( newParent.path() ).
            build().
            execute();

        assertEquals( 2, getVersions( node ).getHits() );
        assertEquals( 1, getVersions( child1 ).getHits() );
        assertEquals( 1, getVersions( child2 ).getHits() );

        assertNull( getNodeByPath( NodePath.newNodePath( node.path(), child1.name().toString() ).build() ) );
        assertNotNull( getNodeByPath( NodePath.newNodePath( movedNode.path(), child1.name().toString() ).build() ) );

        final Node movedChild1 = getNodeById( child1.id() );
        final Node movedChild2 = getNodeById( child2.id() );
        final Node movedChild1_1 = getNodeById( child1_1.id() );

        assertEquals( newParent.path(), movedNode.parentPath() );
        assertEquals( movedNode.path(), movedChild1.parentPath() );
        assertEquals( movedNode.path(), movedChild2.parentPath() );
        assertEquals( movedChild1.path(), movedChild1_1.parentPath() );

        assertEquals( false, movedNode.inheritsPermissions() );
        assertEquals( node.getPermissions(), movedNode.getPermissions() );
    }

    private FindNodeVersionsResult getVersions( final Node node )
    {
        return GetNodeVersionsCommand.create().
            nodeId( node.id() ).
            versionService( this.versionService ).
            build().
            execute();
    }

}