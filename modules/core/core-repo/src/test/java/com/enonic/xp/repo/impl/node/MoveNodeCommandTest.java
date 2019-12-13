package com.enonic.xp.repo.impl.node;

import java.util.Iterator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.MoveNodeException;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodeAlreadyExistAtPathException;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionQuery;
import com.enonic.xp.node.NodeVersionQueryResult;
import com.enonic.xp.node.OperationNotPermittedException;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MoveNodeCommandTest
        extends AbstractNodeTest
        {
            @BeforeEach
    public void setUp()
        throws Exception
    {
        this.createDefaultRootNode();
    }


    @Test
    public void timestamp_updated()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "mynode" ).
            parent( NodePath.ROOT ).
            setNodeId( NodeId.from( "mynode" ) ).
            build() );

        final Node beforeMove = getNodeById( node.id() );

        MoveNodeCommand.create().
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            id( node.id() ).
            newNodeName( NodeName.from( "mynode2" ) ).
            newParent( node.parentPath() ).
            build().
            execute();

        final Node movedNode = getNodeById( node.id() );

        assertTrue( beforeMove.getTimestamp().isBefore( movedNode.getTimestamp() ) );
    }

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
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            id( node.id() ).
            newNodeName( NodeName.from( "mynode2" ) ).
            newParent( node.parentPath() ).
            build().
            execute();

        final Node movedNode = getNodeById( node.id() );

        assertEquals( NodePath.ROOT, movedNode.parentPath() );
        assertEquals( "mynode2", movedNode.name().toString() );
    }


    @Test
    public void move_to_child_as_self_not_allowed()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "mynode" ).
            parent( NodePath.ROOT ).
            setNodeId( NodeId.from( "mynode" ) ).
            build() );

        assertThrows(MoveNodeException.class, () -> MoveNodeCommand.create().
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            id( node.id() ).
            newNodeName( NodeName.from( "mynode2" ) ).
            newParent( node.path() ).
            build().
            execute());
    }

    @Test
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

        assertThrows(MoveNodeException.class, () -> MoveNodeCommand.create().
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            id( node.id() ).
            newNodeName( NodeName.from( "mynode2" ) ).
            newParent( child.path() ).
            build().
            execute());
    }


    @Test
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

        assertThrows(NodeAlreadyExistAtPathException.class, () -> MoveNodeCommand.create().
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            id( node.id() ).
            newNodeName( NodeName.from( "mynode" ) ).
            newParent( newParent.path() ).
            build().
            execute());
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
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
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
        final Node parent = createNode( CreateNodeParams.create().
            name( "parent" ).
            setNodeId( NodeId.from( "parent" ) ).
            parent( NodePath.ROOT ).
            permissions( AccessControlList.of( AccessControlEntry.create().principal( TEST_DEFAULT_USER.getKey() ).allowAll().build() ) ).
            build() );

        final Node child1 = createNode( CreateNodeParams.create().
            name( "child1" ).
            parent( parent.path() ).
            setNodeId( NodeId.from( "child1" ) ).
            inheritPermissions( true ).
            build() );

        final Node child1_1 = createNode( CreateNodeParams.create().
            name( "child1_1" ).
            parent( child1.path() ).
            setNodeId( NodeId.from( "child1_1" ) ).
            build() );

        final Node child1_2 = createNode( CreateNodeParams.create().
            name( "child1_2" ).
            parent( child1.path() ).
            setNodeId( NodeId.from( "child1_2" ) ).
            build() );

        final Node child1_1_1 = createNode( CreateNodeParams.create().
            name( "child1_1_1" ).
            parent( child1_1.path() ).
            setNodeId( NodeId.from( "child1_1_1" ) ).
            build() );

        final Node newParent = createNode( CreateNodeParams.create().
            name( "newParent" ).
            parent( NodePath.ROOT ).
            setNodeId( NodeId.from( "newParent" ) ).
            build() );

        assertEquals( 1, getVersions( child1 ).getHits() );
        assertEquals( 1, getVersions( child1_1 ).getHits() );
        assertEquals( 1, getVersions( child1_2 ).getHits() );
        assertNotNull( getNodeByPath( NodePath.create( child1.path(), child1_1.name().toString() ).build() ) );

        final Node movedNode = MoveNodeCommand.create().
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            id( child1.id() ).
            newParent( newParent.path() ).
            build().
            execute().
            getTargetNode();

        refresh();

        assertEquals( 2, getVersions( child1 ).getHits() );
        assertEquals( 2, getVersions( child1_1 ).getHits() );
        assertEquals( 2, getVersions( child1_2 ).getHits() );

        final NodePath previousChild1Path = child1_1.path();
        assertNull( getNodeByPath( previousChild1Path ) );

        final NodePath newChild1Path = NodePath.create( movedNode.path(), child1_1.name().toString() ).
            build();
        assertNotNull( getNodeByPath( newChild1Path ) );

        final Node movedChild1 = getNodeById( child1_1.id() );
        final Node movedChild2 = getNodeById( child1_2.id() );
        final Node movedChild1_1 = getNodeById( child1_1_1.id() );

        assertEquals( newParent.path(), movedNode.parentPath() );
        assertEquals( movedNode.path(), movedChild1.parentPath() );
        assertEquals( movedNode.path(), movedChild2.parentPath() );
        assertEquals( movedChild1.path(), movedChild1_1.parentPath() );

        assertEquals( false, movedNode.inheritsPermissions() );
        assertEquals( child1.getPermissions(), movedNode.getPermissions() );
    }

    @Test
    public void move_without_permissions()
        throws Exception
    {
        final Node deleteUngrantedNode = createNode( CreateNodeParams.create().
            name( "mynode" ).
            parent( NodePath.ROOT ).
            setNodeId( NodeId.from( "mynode" ) ).
            permissions( AccessControlList.of(
                AccessControlEntry.create().principal( TEST_DEFAULT_USER.getKey() ).allowAll().deny( Permission.MODIFY ).build() ) ).
            build() );

        final Node deleteGrantedNode = createNode( CreateNodeParams.create().
            name( "mynode2" ).
            parent( NodePath.ROOT ).
            setNodeId( NodeId.from( "mynode2" ) ).
            permissions( AccessControlList.of( AccessControlEntry.create().principal( TEST_DEFAULT_USER.getKey() ).allowAll().build() ) ).
            build() );

        final Node createUngrantedNewParent = createNode( CreateNodeParams.create().
            name( "new-parent" ).
            parent( NodePath.ROOT ).
            setNodeId( NodeId.from( "newparent" ) ).
            permissions( AccessControlList.of(
                AccessControlEntry.create().principal( TEST_DEFAULT_USER.getKey() ).allowAll().deny( Permission.CREATE ).build() ) ).
            build() );

        final Node createGrantedNewParent = createNode( CreateNodeParams.create().
            name( "new-parent2" ).
            parent( NodePath.ROOT ).
            setNodeId( NodeId.from( "newparent2" ) ).
            permissions( AccessControlList.of( AccessControlEntry.create().principal( TEST_DEFAULT_USER.getKey() ).allowAll().build() ) ).
            build() );

        // Tests the check of the DELETE right on the moved node
        boolean deleteRightChecked = false;
        try
        {
            MoveNodeCommand.create().
                indexServiceInternal( this.indexServiceInternal ).
                storageService( this.storageService ).
                searchService( this.searchService ).
                id( deleteUngrantedNode.id() ).
                newParent( createGrantedNewParent.path() ).
                build().
                execute();
        }
        catch ( NodeAccessException e )
        {
            deleteRightChecked = true;
        }
        assertTrue( deleteRightChecked );

        // Tests the check of the CREATE right on the new parent
        boolean createRightChecked = false;
        try
        {
            MoveNodeCommand.create().
                indexServiceInternal( this.indexServiceInternal ).
                storageService( this.storageService ).
                searchService( this.searchService ).
                id( deleteGrantedNode.id() ).
                newParent( createUngrantedNewParent.path() ).
                build().
                execute();
        }
        catch ( NodeAccessException e )
        {
            createRightChecked = true;
        }
        assertTrue( createRightChecked );

        // Tests the correct behaviour if both rights are granted
        MoveNodeCommand.create().
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            id( deleteGrantedNode.id() ).
            newParent( createGrantedNewParent.path() ).
            build().
            execute();

        final Node movedNode = getNodeById( deleteGrantedNode.id() );

        assertEquals( createGrantedNewParent.path(), movedNode.parentPath() );
        assertEquals( "mynode2", movedNode.name().toString() );
    }

    @Test
    public void move_to_manually_ordered_parent()
        throws Exception
    {
        final Node originalRoot = createNode( NodePath.ROOT, "a1" );
        final Node a1_1 = createNode( originalRoot.path(), "a1_1" );
        final Node a1_2 = createNode( originalRoot.path(), "a1_2" );

        final Node newParent = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "a2" ).
            childOrder( ChildOrder.manualOrder() ).
            build() );
        final Node a2_1 = createNode( newParent.path(), "a2_1" );
        final Node a2_2 = createNode( newParent.path(), "a2_2" );

        doMoveNode( newParent.path(), a1_1.id() );
        doMoveNode( newParent.path(), a1_2.id() );

        final FindNodesByParentResult result = findByParent( FindNodesByParentParams.create().
            parentId( newParent.id() ).
            build() );

        final Iterator<NodeId> iterator = result.getNodeIds().iterator();

        assertEquals( a1_2.id(), iterator.next() );
        assertEquals( a1_1.id(), iterator.next() );
        assertEquals( a2_2.id(), iterator.next() );
        assertEquals( a2_1.id(), iterator.next() );
    }

    @Test
    public void cannot_move_root_node()
        throws Exception
    {
        assertThrows(OperationNotPermittedException.class, () -> doMoveNode( NodePath.create( "/fisk" ).build(), Node.ROOT_UUID ));
    }

    private void doMoveNode( final NodePath newParent, final NodeId nodeId )
    {
        MoveNodeCommand.create().
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            id( nodeId ).
            newParent( newParent ).
            build().
            execute();
    }

    private NodeVersionQueryResult getVersions( final Node node )
    {
        final NodeVersionQuery query = NodeVersionQuery.create().
            size( 100 ).
            from( 0 ).
            nodeId( node.id() ).
            build();

        return FindNodeVersionsCommand.create().
            query( query ).
            searchService( this.searchService ).
            build().
            execute();
    }

}
