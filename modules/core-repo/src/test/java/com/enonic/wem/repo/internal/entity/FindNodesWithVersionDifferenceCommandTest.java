package com.enonic.wem.repo.internal.entity;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionDiffQuery;
import com.enonic.xp.node.NodeVersionDiffResult;
import com.enonic.xp.node.PushNodesResult;
import com.enonic.xp.node.UpdateNodeParams;

import static org.junit.Assert.*;

public class FindNodesWithVersionDifferenceCommandTest
    extends AbstractNodeTest
{
    private Node rootNode;

    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
        this.rootNode = this.createDefaultRootNode();
    }

    @Test
    public void only_in_source()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "mynode" ).
            parent( NodePath.ROOT ).
            build() );

        assertEquals( 2, getDiff( WS_DEFAULT, WS_OTHER ).getNodesWithDifferences().getSize() );

        doPushNode( WS_OTHER, node );
        doPushNode( WS_OTHER, this.rootNode );

        assertEquals( 0, getDiff( WS_DEFAULT, WS_OTHER ).getNodesWithDifferences().getSize() );
        assertEquals( 0, getDiff( WS_OTHER, WS_DEFAULT ).getNodesWithDifferences().getSize() );
    }

    @Test
    public void only_in_source_with_path()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "mynode" ).
            parent( NodePath.ROOT ).
            build() );

        printBranchIndex();

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
    public void same_version()
        throws Exception
    {
        final Node node = CTX_DEFAULT.callWith( () -> createNode( CreateNodeParams.create().
            name( "mynode" ).
            parent( NodePath.ROOT ).
            build() ) );

        doPushNode( WS_OTHER, rootNode );
        doPushNode( WS_OTHER, node );

        assertEquals( 0, getDiff( WS_DEFAULT, WS_OTHER ).getNodesWithDifferences().getSize() );
        assertEquals( 0, getDiff( WS_OTHER, WS_DEFAULT ).getNodesWithDifferences().getSize() );
    }

    @Test
    public void newer_in_source()
        throws Exception
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
    public void newer_in_target()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "mynode" ).
            parent( NodePath.ROOT ).
            build() );

        doPushNode( WS_OTHER, rootNode );
        doPushNode( WS_OTHER, node );

        CTX_OTHER.runWith( () -> doUpdateNode( node ) );

        assertEquals( 1, getDiff( WS_DEFAULT, WS_OTHER ).getNodesWithDifferences().getSize() );
        assertEquals( 1, getDiff( WS_OTHER, WS_DEFAULT ).getNodesWithDifferences().getSize() );
    }

    @Test
    public void moved_in_source()
        throws Exception
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

        doMoveNode( node, node2 );

        assertEquals( 1, getDiff( WS_DEFAULT, WS_OTHER ).getNodesWithDifferences().getSize() );
        assertEquals( 1, getDiff( WS_OTHER, WS_DEFAULT ).getNodesWithDifferences().getSize() );
    }

    @Test
    public void moved_in_target()
        throws Exception
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

        CTX_OTHER.runWith( () -> doMoveNode( node, node2 ) );

        assertEquals( 1, getDiff( WS_DEFAULT, WS_OTHER ).getNodesWithDifferences().getSize() );
        assertEquals( 1, getDiff( WS_OTHER, WS_DEFAULT ).getNodesWithDifferences().getSize() );
    }

    @Test
    public void deleted_in_source()
        throws Exception
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
    public void deleted_in_target()
        throws Exception
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

        CTX_OTHER.runWith( () -> doDeleteNode( node.id() ) );

        assertEquals( 1, getDiff( WS_DEFAULT, WS_OTHER ).getNodesWithDifferences().getSize() );
        assertEquals( 1, getDiff( WS_OTHER, WS_DEFAULT ).getNodesWithDifferences().getSize() );
    }

    private void doMoveNode( final Node node, final Node newParent )
    {
        MoveNodeCommand.create().
            newParent( newParent.path() ).
            id( node.id() ).
            queryService( this.queryService ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            indexServiceInternal( this.indexServiceInternal ).
            branchService( this.branchService ).
            build().
            execute();
    }

    private NodeVersionDiffResult getDiff( final Branch source, final Branch target )
    {
        return getDiff( source, target, null );
    }


    private NodeVersionDiffResult getDiff( final Branch source, final Branch target, final NodePath nodePath )
    {
        final NodeVersionDiffQuery.Builder queryBuilder = NodeVersionDiffQuery.create().
            target( target ).
            source( source );

        if ( nodePath != null )
        {
            queryBuilder.nodePath( nodePath );
        }

        return FindNodesWithVersionDifferenceCommand.create().
            versionService( this.versionService ).
            query( queryBuilder.build() ).
            build().
            execute();
    }

    private Node doUpdateNode( final Node node )
    {
        final UpdateNodeParams updateNodeParams = UpdateNodeParams.create().
            id( node.id() ).
            editor( editableNode -> editableNode.manualOrderValue = 10l ).
            build();

        return UpdateNodeCommand.create().
            params( updateNodeParams ).
            queryService( this.queryService ).
            indexServiceInternal( this.indexServiceInternal ).
            branchService( this.branchService ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            binaryBlobStore( this.binaryBlobStore ).
            build().
            execute();
    }

    private PushNodesResult doPushNode( final Branch target, final Node createdNode )
    {
        return PushNodesCommand.create().
            ids( NodeIds.from( createdNode.id() ) ).
            target( target ).
            indexServiceInternal( this.indexServiceInternal ).
            branchService( this.branchService ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            queryService( this.queryService ).
            build().
            execute();
    }
}