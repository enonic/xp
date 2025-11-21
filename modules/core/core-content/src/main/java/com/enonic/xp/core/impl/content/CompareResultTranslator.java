package com.enonic.xp.core.impl.content;

import com.enonic.xp.content.CompareContentResult;
import com.enonic.xp.content.CompareContentResults;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodeComparisons;

class CompareResultTranslator
{
    public static CompareContentResults translate( final NodeComparisons nodeComparisons )
    {
        final CompareContentResults.Builder builder = CompareContentResults.create();

        for ( final NodeComparison nodeComparison : nodeComparisons )
        {
            builder.add( new CompareContentResult( Enum.valueOf( CompareStatus.class, nodeComparison.getCompareStatus().name() ),
                                                   ContentId.from( nodeComparison.getNodeId() ) ) );
        }

        return builder.build();
    }

}
