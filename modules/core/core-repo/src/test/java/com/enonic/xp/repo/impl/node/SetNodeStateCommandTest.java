package com.enonic.xp.repo.impl.node;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeState;
import com.enonic.xp.node.SetNodeStateParams;
import com.enonic.xp.node.SetNodeStateResult;

import static org.junit.Assert.*;

public class SetNodeStateCommandTest
    extends AbstractNodeTest
{

    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
        this.createDefaultRootNode();
    }


    @Test
    public void applyRecursive()
        throws Exception
    {
        final Node parent = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "parent" ).
            setNodeId( NodeId.from( "parent" ) ).
            build() );

        final Node child1 = createNode( CreateNodeParams.create().
            parent( parent.path() ).
            name( "child1" ).
            setNodeId( NodeId.from( "child1" ) ).
            build() );

        final Node child1_1 = createNode( CreateNodeParams.create().
            parent( child1.path() ).
            name( "child1_1" ).
            setNodeId( NodeId.from( "child1_1" ) ).
            build() );

        refresh();

        final SetNodeStateResult result = SetNodeStateCommand.create().
            params( SetNodeStateParams.create().
                nodeId( parent.id() ).
                nodeState( NodeState.PENDING_DELETE ).
                recursive( true ).
                build() ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().

            execute();

        assertEquals( 3, result.getUpdatedNodes().getSize() );
    }
}