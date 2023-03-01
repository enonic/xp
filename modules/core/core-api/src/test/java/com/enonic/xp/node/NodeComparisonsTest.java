package com.enonic.xp.node;

import java.time.Instant;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.enonic.xp.content.CompareStatus;

@Disabled("This test does not verify anything")
public class NodeComparisonsTest
{

    @Test
    public void testName()
        throws Exception
    {

        final NodeComparisons.Builder builder = NodeComparisons.create();

        final Runtime runtime = Runtime.getRuntime();

        long usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();
        System.out.println( "Used Memory before: [" + usedMemoryBefore + "]" );
        // working code here

        for ( int i = 0; i < 2_000_000; i++ )
        {
            builder.add( new NodeComparison( createNodeBranchEntry( i ), createNodeBranchEntry( i ), CompareStatus.NEW ) );
        }

        builder.build();

        long usedMemoryAfter = runtime.totalMemory() - runtime.freeMemory();
        System.out.println( "Memory increased: [" + ( usedMemoryAfter - usedMemoryBefore ) + "]" );

    }

    private NodeBranchEntry createNodeBranchEntry( final int i )
    {
        return NodeBranchEntry.create().
            nodeVersionId( NodeVersionId.from( "nodeVersionId_" + i ) ).
            nodePath( new NodePath( "/path" + i ) ).
            nodeId( NodeId.from( "nodeId" + i ) ).
            timestamp( Instant.now() ).
            build();
    }

}
