package com.enonic.xp.lib.node;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.ResolveSyncWorkResult;
import com.enonic.xp.node.SyncWorkResolverParams;

public class DiffBranchesHandlerTest
    extends OldBaseNodeHandlerTest
{

    @Test
    public void testExample1()
    {
        Mockito.when( nodeService.resolveSyncWork( Mockito.isA( SyncWorkResolverParams.class ) ) ).
            thenReturn( ResolveSyncWorkResult.create().
                add( new NodeComparison( createEntry( "a" ), createEntry( "a" ), CompareStatus.NEW ) ).
                add( new NodeComparison( createEntry( "b" ), createEntry( "b" ), CompareStatus.MOVED ) ).
                add( new NodeComparison( createEntry( "c" ), createEntry( "c" ), CompareStatus.OLDER ) ).
                build() );

        runScript( "/site/lib/xp/examples/node/diff-1.js" );
    }

}