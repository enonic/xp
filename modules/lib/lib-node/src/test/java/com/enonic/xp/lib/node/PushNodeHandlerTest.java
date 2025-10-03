package com.enonic.xp.lib.node;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.PushNodeResult;
import com.enonic.xp.node.PushNodesResult;
import com.enonic.xp.node.ResolveSyncWorkResult;
import com.enonic.xp.node.SyncWorkResolverParams;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class PushNodeHandlerTest
    extends BaseNodeHandlerTest
{
    @Test
    void testExample1()
    {
        when( nodeService.push( eq( NodeIds.from( "a" ) ), eq( Branch.from( "otherBranch" ) ) ) )
            .thenReturn( PushNodesResult.create().add( createPushNodeResult( "a", "/a") ).build() );

        runScript( "/lib/xp/examples/node/push-1.js" );
    }

    private static PushNodeResult createPushNodeResult( String a, String targetPath )
    {
        return PushNodeResult.success( NodeId.from( a ), new NodeVersionId(), new NodePath( "/" + a ), new NodePath( targetPath ) );
    }

    @Test
    void testExample2()
    {
        when( nodeService.resolveSyncWork( Mockito.isA( SyncWorkResolverParams.class ) ) ).
            thenReturn( ResolveSyncWorkResult.create().
                add( createNodeComparison("a", "a") ).
                add( createNodeComparison("b", "b") ).
                add( createNodeComparison("c", "c") ).
                build() );

        when( nodeService.push( Mockito.isA( NodeIds.class ), eq( Branch.from( "otherBranch" ) ) ) ).
            thenReturn( PushNodesResult.create()
                            .add( createPushNodeResult( "a", "/b") )
                            .add( createPushNodeResult( "b", "/b")  )
                            .add( createPushNodeResult( "c", "/c") )
                            .add( createNodePushResultFailed( "d", PushNodeResult.Reason.ACCESS_DENIED ) ).
                build() );

        runScript( "/lib/xp/examples/node/push-2.js" );
    }

    @Test
    void testExample3()
    {
        when( nodeService.resolveSyncWork( Mockito.isA( SyncWorkResolverParams.class ) ) ).
            thenReturn( ResolveSyncWorkResult.create().
                add( createNodeComparison("a" ,"a") ).
                add( createNodeComparison("b" ,"b") ).
                add( createNodeComparison("c" ,"c") ).
                build() );

        when( nodeService.push( Mockito.isA( NodeIds.class ), eq( Branch.from( "otherBranch" ) ) ) ).
            thenReturn( PushNodesResult.create()
                            .add( createPushNodeResult( "a", "/a") )
                            .add( createPushNodeResult( "d", "/d") )
                            .build() );

        runScript( "/lib/xp/examples/node/push-3.js" );
    }

    @Test
    public void testExampleWithChildren()
    {
        when( nodeService.resolveSyncWork( Mockito.isA( SyncWorkResolverParams.class ) ) ).
            thenReturn( ResolveSyncWorkResult.create().
            add( createNodeComparison("a", "a") ).
            add( createNodeComparison("b", "b") ).
            add( createNodeComparison("c", "c") ).
            build() );

        when( nodeService.push( eq( NodeIds.create().
            add( NodeId.from( "a" ) ).
            add( NodeId.from( "b" ) ).
            add( NodeId.from( "c" ) ).
            build() ), eq( Branch.from( "otherBranch" ) ) ) ).
            thenReturn( PushNodesResult.create()
                            .add( createPushNodeResult( "a", "/a") )
                            .add( createPushNodeResult( "b", "/b") )
                            .add( createNodePushResultFailed( "c", PushNodeResult.Reason.ACCESS_DENIED ) ).
                build() );

        runScript( "/lib/xp/examples/node/push-4.js" );
    }

    private NodeComparison createNodeComparison( String a, String b )
    {
        return new NodeComparison( NodeId.from( a ), new NodePath( "/" + a ), NodeId.from( b ), new NodePath( "/" + b ),
                                   CompareStatus.NEW );
    }

    private static PushNodeResult createNodePushResultFailed( String a, PushNodeResult.Reason reason )
    {
        return PushNodeResult.failure( NodeId.from( a ), new NodePath( "/" + a ), reason );
    }
}

