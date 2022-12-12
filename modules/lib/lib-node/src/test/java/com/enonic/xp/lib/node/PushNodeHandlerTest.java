package com.enonic.xp.lib.node;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
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
                addSuccess( createEntry( "a" ), NodePath.create("/a").build() ).
                build() );

        runScript( "/lib/xp/examples/node/push-1.js" );
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
                addSuccess( createEntry( "a" ), NodePath.create("/b").build() ).
                addSuccess( createEntry( "b"), NodePath.create("/b").build() ).
                addSuccess( createEntry( "c" ), NodePath.create("/c").build() ).
                addFailed( createEntry( "d" ), PushNodesResult.Reason.ACCESS_DENIED ).
                build() );

        runScript( "/lib/xp/examples/node/push-2.js" );
    }

    @Test
    public void testExample3()
    {
        Mockito.when( nodeService.resolveSyncWork( Mockito.isA( SyncWorkResolverParams.class ) ) ).
            thenReturn( ResolveSyncWorkResult.create().
                add( new NodeComparison( createEntry( "a" ), createEntry( "a" ), CompareStatus.NEW ) ).
                add( new NodeComparison( createEntry( "b" ), createEntry( "b" ), CompareStatus.NEW ) ).
                add( new NodeComparison( createEntry( "c" ), createEntry( "c" ), CompareStatus.NEW ) ).
                build() );

        Mockito.when( nodeService.push( Mockito.isA( NodeIds.class ), Mockito.eq( Branch.from( "otherBranch" ) ) ) ).
            thenReturn( PushNodesResult.create().
                addSuccess( createEntry( "a" ), NodePath.create("/a").build() ).
                addSuccess( createEntry( "d" ), NodePath.create("/d").build() ).
                build() );

        runScript( "/lib/xp/examples/node/push-3.js" );
    }

    @Test
    public void testExampleWithChildren()
    {
        Mockito.when( nodeService.resolveSyncWork( Mockito.isA( SyncWorkResolverParams.class ) ) ).
            thenReturn( ResolveSyncWorkResult.create().
                add( new NodeComparison( createEntry( "a" ), createEntry( "a" ), CompareStatus.NEW ) ).
                add( new NodeComparison( createEntry( "b", "a/b" ), createEntry( "b", "a/b" ), CompareStatus.NEW ) ).
                add( new NodeComparison( createEntry( "c", "a/b/c" ), createEntry( "c", "a/b/c" ), CompareStatus.NEW ) ).
                build() );

        Mockito.when( nodeService.push( Mockito.eq( NodeIds.create().
            add( NodeId.from( "a" ) ).
            add( NodeId.from( "b" ) ).
            add( NodeId.from( "c" ) ).
            build() ), Mockito.eq( Branch.from( "otherBranch" ) ) ) ).
            thenReturn( PushNodesResult.create().
                addSuccess( createEntry( "a" ), NodePath.create("/a").build() ).
                addSuccess( createEntry( "b" ), NodePath.create("/b").build() ).
                addFailed( createEntry( "c" ), PushNodesResult.Reason.ACCESS_DENIED ).
                build() );

        runScript( "/lib/xp/examples/node/push-4.js" );
    }

}

