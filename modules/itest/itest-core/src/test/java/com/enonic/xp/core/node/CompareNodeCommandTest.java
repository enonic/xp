package com.enonic.xp.core.node;

import org.junit.jupiter.api.Test;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.PushNodesResult;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.repo.impl.node.CompareNodeCommand;
import com.enonic.xp.repo.impl.node.DeleteNodeCommand;
import com.enonic.xp.repo.impl.node.PushNodesCommand;
import com.enonic.xp.repo.impl.node.UpdateNodeCommand;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CompareNodeCommandTest
    extends AbstractNodeTest
{
    @Test
    public void status_new()
        throws Exception
    {
        ctxDefault().callWith( this::createDefaultRootNode );

        final Node createdNode = ctxDefault().callWith( () -> createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() ) );

        final NodeComparison comparison = ctxDefault().callWith( () -> doCompare( WS_OTHER, createdNode ) );

        assertEquals( CompareStatus.NEW, comparison.getCompareStatus() );
    }

    @Test
    public void status_new_target()
        throws Exception
    {
        ctxOther().callWith( this::createDefaultRootNode );

        final Node createdNode = ctxOther().callWith( () -> createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() ) );

        final NodeComparison comparison = ctxDefault().callWith( () -> doCompare( WS_OTHER, createdNode ) );

        assertEquals( CompareStatus.NEW_TARGET, comparison.getCompareStatus() );
    }

    @Test
    public void status_capital_node_id()
        throws Exception
    {
        ctxOther().callWith( this::createDefaultRootNode );

        final Node createdNode = ctxOther().callWith( () -> createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            setNodeId( NodeId.from( "MyNodeId" ) ).
            build() ) );

        final NodeComparison comparison = ctxDefault().callWith( () -> doCompare( WS_OTHER, createdNode ) );

        assertEquals( CompareStatus.NEW_TARGET, comparison.getCompareStatus() );
    }

    @Test
    public void status_deleted_stage_yields_new_in_target()
        throws Exception
    {
        ctxOther().callWith( this::createDefaultRootNode );

        final Node createdNode = ctxOther().callWith( () -> createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() ) );

        assertNotNull( ctxOther().callWith( () -> getNodeById( createdNode.id() ) ) );

        ctxOther().runWith( () -> doPushNode( WS_DEFAULT, createdNode ) );

        assertNotNull( ctxDefault().callWith( () -> getNodeById( createdNode.id() ) ) );

        DeleteNodeCommand.create().
            nodeId( createdNode.id() ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        final NodeComparison comparison = ctxDefault().callWith( () -> doCompare( WS_OTHER, createdNode ) );

        assertEquals( CompareStatus.NEW_TARGET, comparison.getCompareStatus() );
    }


    @Test
    public void status_equal()
        throws Exception
    {
        ctxDefault().callWith( this::createDefaultRootNode );

        final Node createdNode = ctxDefault().callWith( () -> createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() ) );

        ctxDefault().runWith( () -> doPushNode( WS_OTHER, createdNode ) );

        final NodeComparison comparison = ctxDefault().callWith( () -> doCompare( WS_OTHER, createdNode ) );

        assertEquals( CompareStatus.EQUAL, comparison.getCompareStatus() );
    }

    @Test
    public void status_newer()
        throws Exception
    {
        ctxDefault().callWith( this::createDefaultRootNode );

        final Node createdNode = ctxDefault().callWith( () -> createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() ) );

        ctxDefault().runWith( () -> doPushNode( WS_OTHER, createdNode ) );
        refresh();

        ctxDefault().runWith( () -> doUpdateNode( createdNode ) );
        refresh();

        final NodeComparison comparison = ctxDefault().callWith( () -> doCompare( WS_OTHER, createdNode ) );

        assertEquals( CompareStatus.NEWER, comparison.getCompareStatus() );
    }

    @Test
    public void status_older()
        throws Exception
    {
        ctxDefault().callWith( this::createDefaultRootNode );

        final Node createdNode = ctxDefault().callWith( () -> createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() ) );

        ctxDefault().runWith( () -> doPushNode( WS_OTHER, createdNode ) );
        refresh();

        ctxOther().runWith( () -> doUpdateNode( createdNode ) );
        refresh();

        final NodeComparison comparison = ctxDefault().callWith( () -> doCompare( WS_OTHER, createdNode ) );

        assertEquals( CompareStatus.OLDER, comparison.getCompareStatus() );
    }


    @Test
    public void status_moved_source()
        throws Exception
    {
        ctxDefault().callWith( this::createDefaultRootNode );

        final Node createdNode = ctxDefault().callWith( () -> createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() ) );

        final Node mySecondNode = ctxDefault().callWith( () -> createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-second-node" ).
            build() ) );

        ctxDefault().runWith( () -> doPushNode( WS_OTHER, createdNode ) );
        refresh();

        ctxDefault().runWith( () -> moveNode( createdNode.id(), mySecondNode.path() ) );

        final NodeComparison comparison = ctxDefault().callWith( () -> doCompare( WS_OTHER, createdNode ) );

        assertEquals( CompareStatus.MOVED, comparison.getCompareStatus() );
    }


    private NodeComparison doCompare( final Branch branch, final Node createdNode )
    {
        return CompareNodeCommand.create().
            storageService( this.storageService ).
            nodeId( createdNode.id() ).
            target( branch ).
            build().
            execute();
    }

    private Node doUpdateNode( final Node createdNode )
    {
        final UpdateNodeParams updateNodeParams = UpdateNodeParams.create().
            id( createdNode.id() ).
            editor( editableNode -> editableNode.manualOrderValue = 10L ).
            build();

        return UpdateNodeCommand.create().
            params( updateNodeParams ).
            indexServiceInternal( this.indexServiceInternal ).
            binaryService( this.binaryService ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().execute().getResult( ContextAccessor.current().getBranch() );
    }

    private PushNodesResult doPushNode( final Branch branch, final Node createdNode )
    {
        return PushNodesCommand.create().
            ids( NodeIds.from( createdNode.id() ) ).
            target( branch ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();
    }

}
