package com.enonic.xp.core.node;

import java.util.Iterator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.CreateNodeParams;
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
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.repo.impl.node.FindNodeVersionsCommand;
import com.enonic.xp.repo.impl.node.MoveNodeCommand;
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
        final Node node =
            createNode( CreateNodeParams.create().name( "mynode" ).parent( NodePath.ROOT ).setNodeId( NodeId.from( "mynode" ) ).build() );

        final Node beforeMove = getNodeById( node.id() );

        MoveNodeCommand.create()
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .id( node.id() )
            .newNodeName( NodeName.from( "mynode2" ) )
            .newParent( node.parentPath() )
            .build()
            .execute();

        final Node movedNode = getNodeById( node.id() );

        assertTrue( beforeMove.getTimestamp().isBefore( movedNode.getTimestamp() ) );
    }

    @Test
    public void new_name_only()
        throws Exception
    {
        final Node node =
            createNode( CreateNodeParams.create().name( "mynode" ).parent( NodePath.ROOT ).setNodeId( NodeId.from( "mynode" ) ).build() );

        MoveNodeCommand.create()
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .id( node.id() )
            .newNodeName( NodeName.from( "mynode2" ) )
            .newParent( node.parentPath() )
            .build()
            .execute();

        final Node movedNode = getNodeById( node.id() );

        assertEquals( NodePath.ROOT, movedNode.parentPath() );
        assertEquals( "mynode2", movedNode.name().toString() );
    }


    @Test
    public void move_to_child_as_self_not_allowed()
        throws Exception
    {
        final Node node =
            createNode( CreateNodeParams.create().name( "mynode" ).parent( NodePath.ROOT ).setNodeId( NodeId.from( "mynode" ) ).build() );

        assertThrows( MoveNodeException.class, () -> MoveNodeCommand.create()
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .id( node.id() )
            .newNodeName( NodeName.from( "mynode2" ) )
            .newParent( node.path() )
            .build()
            .execute() );
    }

    @Test
    public void move_to_child_of_own_child_not_allowed()
        throws Exception
    {
        final Node node =
            createNode( CreateNodeParams.create().name( "mynode" ).parent( NodePath.ROOT ).setNodeId( NodeId.from( "mynode" ) ).build() );

        final Node child =
            createNode( CreateNodeParams.create().name( "child" ).parent( node.path() ).setNodeId( NodeId.from( "child" ) ).build() );

        assertThrows( MoveNodeException.class, () -> MoveNodeCommand.create()
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .id( node.id() )
            .newNodeName( NodeName.from( "mynode2" ) )
            .newParent( child.path() )
            .build()
            .execute() );
    }


    @Test
    public void move_node_already_exists()
        throws Exception
    {
        final Node node =
            createNode( CreateNodeParams.create().name( "mynode" ).parent( NodePath.ROOT ).setNodeId( NodeId.from( "mynode" ) ).build() );

        final Node newParent = createNode(
            CreateNodeParams.create().name( "new-parent" ).parent( NodePath.ROOT ).setNodeId( NodeId.from( "newparent" ) ).build() );

        createNode( CreateNodeParams.create().name( "mynode" ).parent( newParent.path() ).build() );

        assertThrows( NodeAlreadyExistAtPathException.class, () -> MoveNodeCommand.create()
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .id( node.id() )
            .newNodeName( NodeName.from( "mynode" ) )
            .newParent( newParent.path() )
            .build()
            .execute() );
    }

    @Test
    public void move_to_new_parent()
        throws Exception
    {
        final Node node =
            createNode( CreateNodeParams.create().name( "mynode" ).parent( NodePath.ROOT ).setNodeId( NodeId.from( "mynode" ) ).build() );

        final Node newParent = createNode(
            CreateNodeParams.create().name( "new-parent" ).parent( NodePath.ROOT ).setNodeId( NodeId.from( "newparent" ) ).build() );

        MoveNodeCommand.create()
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .id( node.id() )
            .newParent( newParent.path() )
            .build()
            .execute();

        final Node movedNode = getNodeById( node.id() );

        assertEquals( newParent.path(), movedNode.parentPath() );
        assertEquals( "mynode", movedNode.name().toString() );
    }

    @Test
    public void move_with_children()
        throws Exception
    {
        final Node parent = createNode( CreateNodeParams.create()
                                            .name( "parent" )
                                            .setNodeId( NodeId.from( "parent" ) )
                                            .parent( NodePath.ROOT )
                                            .permissions( AccessControlList.of(
                                                AccessControlEntry.create().principal( TEST_DEFAULT_USER.getKey() ).allowAll().build() ) )
                                            .build() );

        final Node child1 = createNode( CreateNodeParams.create()
                                            .name( "child1" )
                                            .parent( parent.path() )
                                            .setNodeId( NodeId.from( "child1" ) )
                                            .build() );

        final Node child1_1 = createNode(
            CreateNodeParams.create().name( "child1_1" ).parent( child1.path() ).setNodeId( NodeId.from( "child1_1" ) ).build() );

        final Node child1_2 = createNode(
            CreateNodeParams.create().name( "child1_2" ).parent( child1.path() ).setNodeId( NodeId.from( "child1_2" ) ).build() );

        final Node child1_1_1 = createNode(
            CreateNodeParams.create().name( "child1_1_1" ).parent( child1_1.path() ).setNodeId( NodeId.from( "child1_1_1" ) ).build() );

        final Node newParent = createNode(
            CreateNodeParams.create().name( "newParent" ).parent( NodePath.ROOT ).setNodeId( NodeId.from( "newParent" ) ).build() );

        final Node movedNode = MoveNodeCommand.create()
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .id( child1.id() )
            .newParent( newParent.path() )
            .refresh( RefreshMode.ALL )
            .build()
            .execute()
            .getMovedNodes()
            .get( 0 )
            .getNode();


        assertEquals( 2, getVersions( child1 ).getHits() );
        assertEquals( 2, getVersions( child1_1 ).getHits() );
        assertEquals( 2, getVersions( child1_2 ).getHits() );

        final NodePath previousChild1Path = child1_1.path();
        assertNull( getNodeByPath( previousChild1Path ) );

        final NodePath newChild1Path = new NodePath( movedNode.path(), child1_1.name() );
        assertNotNull( getNodeByPath( newChild1Path ) );

        final Node movedChild1 = getNodeById( child1_1.id() );
        final Node movedChild2 = getNodeById( child1_2.id() );
        final Node movedChild1_1 = getNodeById( child1_1_1.id() );

        assertEquals( newParent.path(), movedNode.parentPath() );
        assertEquals( movedNode.path(), movedChild1.parentPath() );
        assertEquals( movedNode.path(), movedChild2.parentPath() );
        assertEquals( movedChild1.path(), movedChild1_1.parentPath() );

        assertEquals( child1.getPermissions(), movedNode.getPermissions() );
    }

    @Test
    public void move_with_processed_data()
        throws Exception
    {
        final Node parent = createNode( CreateNodeParams.create()
                                            .name( "parent" )
                                            .setNodeId( NodeId.from( "parent" ) )
                                            .parent( NodePath.ROOT )
                                            .permissions( AccessControlList.of(
                                                AccessControlEntry.create().principal( TEST_DEFAULT_USER.getKey() ).allowAll().build() ) )
                                            .build() );

        final Node child1 = createNode( CreateNodeParams.create()
                                            .name( "child1" )
                                            .parent( parent.path() )
                                            .setNodeId( NodeId.from( "child1" ) )
                                            .build() );

        final Node child1_1 = createNode(
            CreateNodeParams.create().name( "child1_1" ).parent( child1.path() ).setNodeId( NodeId.from( "child1_1" ) ).build() );

        final Node child1_1_1 = createNode(
            CreateNodeParams.create().name( "child1_1_1" ).parent( child1_1.path() ).setNodeId( NodeId.from( "child1_1_1" ) ).build() );

        final Node newParent = createNode(
            CreateNodeParams.create().name( "newParent" ).parent( NodePath.ROOT ).setNodeId( NodeId.from( "newParent" ) ).build() );

        final Node movedNode = MoveNodeCommand.create()
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .refresh( RefreshMode.ALL )
            .id( child1.id() )
            .newParent( newParent.path() )
            .processor( ( data ) -> {
                data.addString( "field", "value" );
                return data;
            } )
            .build()
            .execute()
            .getMovedNodes()
            .get( 0 )
            .getNode();

        assertEquals( 2, getVersions( child1 ).getHits() );
        assertEquals( 2, getVersions( child1_1 ).getHits() );
        assertEquals( 2, getVersions( child1_1_1 ).getHits() );

        final NodePath previousChild1Path = child1_1.path();
        assertNull( getNodeByPath( previousChild1Path ) );

        final NodePath newChild1Path = new NodePath( movedNode.path(), child1_1.name() );
        assertNotNull( getNodeByPath( newChild1Path ) );

        final Node movedChild1_1 = getNodeById( child1_1.id() );
        final Node movedChild1_1_1 = getNodeById( child1_1_1.id() );

        assertEquals( "value", movedNode.data().getString( "field" ) );
        assertEquals( "value", movedChild1_1.data().getString( "field" ) );
        assertEquals( "value", movedChild1_1_1.data().getString( "field" ) );
    }

    @Test
    public void move_without_permissions()
        throws Exception
    {
        final Node cannonMoveNode = createNode( CreateNodeParams.create()
                                                    .name( "mynode" )
                                                    .parent( NodePath.ROOT )
                                                    .setNodeId( NodeId.from( "mynode" ) )
                                                    .permissions( AccessControlList.of( AccessControlEntry.create()
                                                                                            .principal( TEST_DEFAULT_USER.getKey() )
                                                                                            .allowAll()
                                                                                            .deny( Permission.MODIFY )
                                                                                            .build() ) )
                                                    .build() );

        final Node canMoveNode = createNode( CreateNodeParams.create()
                                                 .name( "mynode2" )
                                                 .parent( NodePath.ROOT )
                                                 .setNodeId( NodeId.from( "mynode2" ) )
                                                 .permissions( AccessControlList.of( AccessControlEntry.create()
                                                                                         .principal( TEST_DEFAULT_USER.getKey() )
                                                                                         .allowAll()
                                                                                         .build() ) )
                                                 .build() );

        final Node cannotMoveIntoNode = createNode( CreateNodeParams.create()
                                                        .name( "new-parent" )
                                                        .parent( NodePath.ROOT )
                                                        .setNodeId( NodeId.from( "newparent" ) )
                                                        .permissions( AccessControlList.of( AccessControlEntry.create()
                                                                                                .principal( TEST_DEFAULT_USER.getKey() )
                                                                                                .allowAll()
                                                                                                .deny( Permission.CREATE )
                                                                                                .build() ) )
                                                        .build() );

        final Node canMoveIntoNode = createNode( CreateNodeParams.create()
                                                     .name( "new-parent2" )
                                                     .parent( NodePath.ROOT )
                                                     .setNodeId( NodeId.from( "newparent2" ) )
                                                     .permissions( AccessControlList.of( AccessControlEntry.create()
                                                                                             .principal( TEST_DEFAULT_USER.getKey() )
                                                                                             .allowAll()
                                                                                             .build() ) )
                                                     .build() );

        // Tests the check of the MODIFY right on the moved node
        assertThrows( NodeAccessException.class, MoveNodeCommand.create()
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .id( cannonMoveNode.id() )
            .newParent( canMoveIntoNode.path() )
            .build()::execute );

        // Tests the check of the CREATE right on the new parent
        assertThrows( NodeAccessException.class, MoveNodeCommand.create()
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .id( canMoveNode.id() )
            .newParent( cannotMoveIntoNode.path() )
            .build()::execute );
    }

    @Test
    public void move_with_hidden_node()
    {
        final Node nodeToMove = createNode( CreateNodeParams.create()
                                                .name( "mynode2" )
                                                .parent( NodePath.ROOT )
                                                .setNodeId( NodeId.from( "mynode2" ) )
                                                .permissions( AccessControlList.of( AccessControlEntry.create()
                                                                                        .principal( TEST_DEFAULT_USER.getKey() )
                                                                                        .allowAll()
                                                                                        .build() ) )
                                                .build() );

        // This node is not visible to user, but moved together with parent node anyway.
        final Node hiddenChild = createNode( CreateNodeParams.create()
                                                 .name( "hiddenNode" )
                                                 .parent( nodeToMove.path() )
                                                 .setNodeId( NodeId.from( "myhiddennode" ) )
                                                 .permissions( AccessControlList.of(
                                                     AccessControlEntry.create().principal( TEST_DEFAULT_USER.getKey() ).build() ) )
                                                 .build() );

        final Node newParent = createNode( CreateNodeParams.create()
                                               .name( "new-parent2" )
                                               .parent( NodePath.ROOT )
                                               .setNodeId( NodeId.from( "newparent2" ) )
                                               .permissions( AccessControlList.of( AccessControlEntry.create()
                                                                                       .principal( TEST_DEFAULT_USER.getKey() )
                                                                                       .allowAll()
                                                                                       .build() ) )
                                               .build() );

        MoveNodeCommand.create()
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .id( nodeToMove.id() )
            .newParent( newParent.path() )
            .build()
            .execute();

        final Node movedNode = getNodeById( nodeToMove.id() );

        assertEquals( newParent.path(), movedNode.parentPath() );
        assertEquals( "mynode2", movedNode.name().toString() );

        final Node hiddenNode = ctxDefaultAdmin().callWith( () -> getNodeById( hiddenChild.id() ) );

        assertEquals( movedNode.path(), hiddenNode.parentPath() );
    }

    @Test
    public void move_to_manually_ordered_parent()
        throws Exception
    {
        final Node originalRoot = createNode( NodePath.ROOT, "a1" );
        final Node a1_1 = createNode( originalRoot.path(), "a1_1" );
        final Node a1_2 = createNode( originalRoot.path(), "a1_2" );

        final Node newParent =
            createNode( CreateNodeParams.create().parent( NodePath.ROOT ).name( "a2" ).childOrder( ChildOrder.manualOrder() ).build() );
        final Node a2_1 = createNode( newParent.path(), "a2_1" );
        final Node a2_2 = createNode( newParent.path(), "a2_2" );
        refresh();

        doMoveNode( newParent.path(), a1_1.id() );
        doMoveNode( newParent.path(), a1_2.id() );

        refresh();

        final FindNodesByParentResult result = findByParent( newParent.path() );

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
        assertThrows( OperationNotPermittedException.class, () -> doMoveNode( new NodePath( "/fisk" ), Node.ROOT_UUID ) );
    }

    private void doMoveNode( final NodePath newParent, final NodeId nodeId )
    {
        MoveNodeCommand.create()
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .id( nodeId )
            .newParent( newParent )
            .build()
            .execute();
    }

    private NodeVersionQueryResult getVersions( final Node node )
    {
        final NodeVersionQuery query = NodeVersionQuery.create().size( 100 ).from( 0 ).nodeId( node.id() ).build();

        return FindNodeVersionsCommand.create().query( query ).searchService( this.searchService ).build().execute();
    }

}
