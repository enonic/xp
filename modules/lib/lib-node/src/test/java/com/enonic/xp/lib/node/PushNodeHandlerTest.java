package com.enonic.xp.lib.node;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeState;
import com.enonic.xp.node.PushNodesResult;
import com.enonic.xp.node.ResolveSyncWorkResult;
import com.enonic.xp.node.SyncWorkResolverParams;

public class PushNodeHandlerTest
    extends BaseNodeHandlerTest
{
    @Test
    public void testExample1()
    {
        Mockito.when( nodeService.push( Mockito.eq( NodeIds.from( "a" ) ), Mockito.eq( Branch.from( "otherBranch" ) ) ) ).
            thenReturn( PushNodesResult.create().
                addSuccess( createEntry( "a" ) ).
                build() );

        runScript( "/site/lib/xp/examples/node/push-1.js" );
    }

    @Test
    public void testExample2()
    {
        Mockito.when( nodeService.resolveSyncWork( Mockito.isA( SyncWorkResolverParams.class ) ) ).
            thenReturn( ResolveSyncWorkResult.create().
                add( new NodeComparison( createEntry( "a" ), createEntry( "a" ), CompareStatus.NEW ) ).
                add( new NodeComparison( createEntry( "b" ), createEntry( "b" ), CompareStatus.NEW ) ).
                add( new NodeComparison( createEntry( "c" ), createEntry( "c" ), CompareStatus.NEW ) ).
                build() );

        Mockito.when( nodeService.push( Mockito.isA( NodeIds.class ), Mockito.eq( Branch.from( "otherBranch" ) ) ) ).
            thenReturn( PushNodesResult.create().
                addSuccess( createEntry( "a" ) ).
                addSuccess( createEntry( "b" ) ).
                addSuccess( createEntry( "c" ) ).
                addFailed( createEntry( "d" ), PushNodesResult.Reason.ACCESS_DENIED ).
                build() );

        runScript( "/site/lib/xp/examples/node/push-2.js" );
    }

    private NodeBranchEntry createEntry( final String id )
    {
        return NodeBranchEntry.create().
            nodeId( NodeId.from( id ) ).
            nodeState( NodeState.DEFAULT ).
            nodePath( NodePath.create( NodePath.ROOT + id ).
                build() ).
            build();
    }
}

