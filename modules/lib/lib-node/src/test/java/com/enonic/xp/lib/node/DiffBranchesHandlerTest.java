package com.enonic.xp.lib.node;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.ResolveSyncWorkResult;
import com.enonic.xp.node.SyncWorkResolverParams;

class DiffBranchesHandlerTest
    extends BaseNodeHandlerTest
{
    @Test
    void testExample1()
    {
        Mockito.when( nodeService.resolveSyncWork( Mockito.isA( SyncWorkResolverParams.class ) ) ).
            thenReturn( ResolveSyncWorkResult.create().
                add( createNodeComparison( "a", "a", CompareStatus.NEW ) ).
                add( createNodeComparison( "b" , "b", CompareStatus.MOVED ) ).
                add( createNodeComparison( "c" , "c", CompareStatus.OLDER ) ).
                build() );

        runScript( "/lib/xp/examples/node/diff-1.js" );
    }

    private NodeComparison createNodeComparison( String a, String b, CompareStatus status )
    {
        return new NodeComparison( NodeId.from( a ), new NodePath( "/" + a ), NodeId.from( b ), new NodePath( "/" + b ),
                                   status );
    }
}
