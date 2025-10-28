package com.enonic.xp.core.node;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionDiffResult;
import com.enonic.xp.node.PatchNodeParams;
import com.enonic.xp.node.PushNodesResult;
import com.enonic.xp.repo.impl.node.FindNodesWithVersionDifferenceCommand;
import com.enonic.xp.repo.impl.node.PatchNodeCommand;
import com.enonic.xp.repo.impl.node.PushNodesCommand;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FindNodesWithVersionDifferenceCommandTest
    extends AbstractNodeTest
{
    private Node rootNode;

    @BeforeEach
    void setUp()
    {
        this.rootNode = this.createDefaultRootNode();
    }

    @Test
    void only_in_source()
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "mynode" ).
            parent( NodePath.ROOT ).
            build() );

        refresh();

        assertEquals( 2, getDiff( WS_DEFAULT, WS_OTHER ).getNodesWithDifferences().getSize() );

        doPushNode( WS_OTHER, node );
        doPushNode( WS_OTHER, this.rootNode );

        assertEquals( 0, getDiff( WS_DEFAULT, WS_OTHER ).getNodesWithDifferences().getSize() );
        assertEquals( 0, getDiff( WS_OTHER, WS_DEFAULT ).getNodesWithDifferences().getSize() );
    }

    @Test
    void only_in_source_with_path()
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "mynode" ).
            parent( NodePath.ROOT ).
            build() );

        final Node child1 = createNode( CreateNodeParams.create().
            name( "mynode" ).
            parent( node.path() ).
            build() );

        final Node child1_1 = createNode( CreateNodeParams.create().
            name( "mynode" ).
            parent( child1.path() ).
            build() );

        final Node child1_1_1 = createNode( CreateNodeParams.create().
            name( "mynode" ).
            parent( child1_1.path() ).
            build() );

        assertEquals( 5, getDiff( WS_DEFAULT, WS_OTHER, NodePath.ROOT ).getNodesWithDifferences().getSize() );
        assertEquals( 4, getDiff( WS_DEFAULT, WS_OTHER, node.path() ).getNodesWithDifferences().getSize() );
        assertEquals( 3, getDiff( WS_DEFAULT, WS_OTHER, child1.path() ).getNodesWithDifferences().getSize() );
        assertEquals( 2, getDiff( WS_DEFAULT, WS_OTHER, child1_1.path() ).getNodesWithDifferences().getSize() );
        assertEquals( 1, getDiff( WS_DEFAULT, WS_OTHER, child1_1_1.path() ).getNodesWithDifferences().getSize() );

        doPushNode( WS_OTHER, node );
        doPushNode( WS_OTHER, child1 );
        doPushNode( WS_OTHER, child1_1 );

        assertEquals( 1, getDiff( WS_DEFAULT, WS_OTHER, node.path() ).getNodesWithDifferences().getSize() );
    }

    @Test
    void same_version()
    {
        final Node node = ctxDefault().callWith( () -> createNode( CreateNodeParams.create().
            name( "mynode" ).
            parent( NodePath.ROOT ).
            build() ) );

        doPushNode( WS_OTHER, rootNode );
        doPushNode( WS_OTHER, node );

        assertEquals( 0, getDiff( WS_DEFAULT, WS_OTHER ).getNodesWithDifferences().getSize() );
        assertEquals( 0, getDiff( WS_OTHER, WS_DEFAULT ).getNodesWithDifferences().getSize() );
    }

    @Test
    void newer_in_source()
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "mynode" ).
            parent( NodePath.ROOT ).
            build() );

        doPushNode( WS_OTHER, rootNode );
        doPushNode( WS_OTHER, node );

        doUpdateNode( node );

        assertEquals( 1, getDiff( WS_DEFAULT, WS_OTHER ).getNodesWithDifferences().getSize() );
        assertEquals( 1, getDiff( WS_OTHER, WS_DEFAULT ).getNodesWithDifferences().getSize() );
    }

    @Test
    void newer_in_target()
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "mynode" ).
            parent( NodePath.ROOT ).
            build() );

        doPushNode( WS_OTHER, rootNode );
        doPushNode( WS_OTHER, node );

        ctxOther().runWith( () -> doUpdateNode( node ) );

        assertEquals( 1, getDiff( WS_DEFAULT, WS_OTHER ).getNodesWithDifferences().getSize() );
        assertEquals( 1, getDiff( WS_OTHER, WS_DEFAULT ).getNodesWithDifferences().getSize() );
    }

    @Test
    void moved_in_source()
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "mynode" ).
            parent( NodePath.ROOT ).
            build() );

        final Node node2 = createNode( CreateNodeParams.create().
            name( "mynode2" ).
            parent( NodePath.ROOT ).
            build() );

        doPushNode( WS_OTHER, rootNode );
        doPushNode( WS_OTHER, node );
        doPushNode( WS_OTHER, node2 );

        assertEquals( 0, getDiff( WS_DEFAULT, WS_OTHER ).getNodesWithDifferences().getSize() );

        moveNode( node.id(), node2.path() );

        assertEquals( 1, getDiff( WS_DEFAULT, WS_OTHER ).getNodesWithDifferences().getSize() );
        assertEquals( 1, getDiff( WS_OTHER, WS_DEFAULT ).getNodesWithDifferences().getSize() );
    }

    @Test
    void moved_in_target()
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "mynode" ).
            parent( NodePath.ROOT ).
            build() );

        final Node node2 = createNode( CreateNodeParams.create().
            name( "mynode2" ).
            parent( NodePath.ROOT ).
            build() );

        doPushNode( WS_OTHER, rootNode );
        doPushNode( WS_OTHER, node );
        doPushNode( WS_OTHER, node2 );

        assertEquals( 0, getDiff( WS_DEFAULT, WS_OTHER ).getNodesWithDifferences().getSize() );

        ctxOther().runWith( () -> moveNode( node.id(), node2.path() ) );

        assertEquals( 1, getDiff( WS_DEFAULT, WS_OTHER ).getNodesWithDifferences().getSize() );
        assertEquals( 1, getDiff( WS_OTHER, WS_DEFAULT ).getNodesWithDifferences().getSize() );
    }

    @Test
    void deleted_in_source()
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "mynode" ).
            parent( NodePath.ROOT ).
            build() );

        final Node node2 = createNode( CreateNodeParams.create().
            name( "mynode2" ).
            parent( NodePath.ROOT ).
            build() );

        doPushNode( WS_OTHER, rootNode );
        doPushNode( WS_OTHER, node );
        doPushNode( WS_OTHER, node2 );

        assertEquals( 0, getDiff( WS_DEFAULT, WS_OTHER ).getNodesWithDifferences().getSize() );

        doDeleteNode( node.id() );

        assertEquals( 1, getDiff( WS_DEFAULT, WS_OTHER ).getNodesWithDifferences().getSize() );
        //assertEquals( 1, getDiff( WS_OTHER, WS_DEFAULT ).getNodesWithDifferences().getSize() );
    }

    @Test
    void assure_correct_diff_order_when_there_are_children()
    {
        final Node node1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "dddd" ) ).
            parent( NodePath.ROOT ).
            name( "dddd" ).
            build() );

        final Node node1_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "ccc" ) ).
            parent( node1.path() ).
            name( "ccc" ).
            build() );

        final Node node1_1_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "11" ) ).
            parent( node1_1.path() ).
            name( "11" ).
            build() );

        final Node node1_1_1_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "_a" ) ).
            parent( node1_1_1.path() ).
            name( "_a" ).
            build() );

        final Node node2 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node2" ) ).
            parent( NodePath.ROOT ).
            name( "node2" ).
            build() );

        pushNodes( WS_OTHER, node1.id(), node1_1_1_1.id(), node1_1_1.id(), node1_1.id(), node2.id() );

        ctxOther().runWith( () -> doUpdateNode( node1_1_1_1 ) );
        ctxOther().runWith( () -> doUpdateNode( node1_1_1 ) );
        ctxOther().runWith( () -> doUpdateNode( node1_1 ) );
        ctxOther().runWith( () -> doUpdateNode( node1 ) );

        NodeVersionDiffResult result = getDiff( WS_DEFAULT, WS_OTHER, node1.path() );

        assertEquals( 4, result.getNodesWithDifferences().getSize() );

        int counter = 0;
        for ( final NodeId nodeId : result.getNodesWithDifferences() )
        {
            if ( counter == 0 )
            {

                assertEquals( "dddd", nodeId.toString() );
            }
            else if ( counter == 1 )
            {
                assertEquals( "ccc", nodeId.toString() );
            }
            else if ( counter == 2 )
            {
                assertEquals( "11", nodeId.toString() );
            }
            else if ( counter == 3 )
            {
                assertEquals( "_a", nodeId.toString() );
            }

            counter++;
        }
    }

    @Test
    void deleted_in_target()
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "mynode" ).
            parent( NodePath.ROOT ).
            build() );

        final Node node2 = createNode( CreateNodeParams.create().
            name( "mynode2" ).
            parent( NodePath.ROOT ).
            build() );

        doPushNode( WS_OTHER, rootNode );
        doPushNode( WS_OTHER, node );
        doPushNode( WS_OTHER, node2 );

        assertEquals( 0, getDiff( WS_DEFAULT, WS_OTHER ).getNodesWithDifferences().getSize() );

        ctxOther().runWith( () -> doDeleteNode( node.id() ) );

        assertEquals( 1, getDiff( WS_DEFAULT, WS_OTHER ).getNodesWithDifferences().getSize() );
        assertEquals( 1, getDiff( WS_OTHER, WS_DEFAULT ).getNodesWithDifferences().getSize() );
    }

    private NodeVersionDiffResult getDiff( final Branch source, final Branch target )
    {
        return getDiff( source, target, null );
    }


    private NodeVersionDiffResult getDiff( final Branch source, final Branch target, final NodePath nodePath )
    {
        refresh();

        final FindNodesWithVersionDifferenceCommand.Builder commandBuilder = FindNodesWithVersionDifferenceCommand.create().
            searchService( this.searchService ).
            storageService( this.storageService ).
            target( target ).
            source( source );

        if ( nodePath != null )
        {
            commandBuilder.nodePath( nodePath );
        }

        return commandBuilder.build().
            execute();
    }

    private Node doUpdateNode( final Node node )
    {
        final PatchNodeParams updateNodeParams = PatchNodeParams.create().
            id( node.id() ).
            editor( editableNode -> editableNode.manualOrderValue = 10L ).
            build();

        return PatchNodeCommand.create().
            params( updateNodeParams ).binaryService( this.binaryService ).indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().execute().getResult( ContextAccessor.current().getBranch() );
    }

    private PushNodesResult doPushNode( final Branch target, final Node createdNode )
    {
        return PushNodesCommand.create().
            ids( NodeIds.from( createdNode.id() ) ).
            target( target ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();
    }
}
