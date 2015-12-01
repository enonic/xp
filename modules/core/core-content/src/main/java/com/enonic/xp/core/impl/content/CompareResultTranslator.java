package com.enonic.xp.core.impl.content;

import com.enonic.xp.content.CompareContentResult;
import com.enonic.xp.content.CompareContentResults;
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
            builder.add( doTranslate( nodeComparison ) );
        }

        return builder.build();
    }

    public static CompareContentResult translate( final NodeComparison nodeComparison )
    {
        return doTranslate( nodeComparison );
    }

    private static CompareContentResult doTranslate( final NodeComparison nodeComparison )
    {
        return new CompareContentResult( nodeComparison.getCompareStatus(), ContentId.from( nodeComparison.getNodeId().toString() ) );
    }

}
