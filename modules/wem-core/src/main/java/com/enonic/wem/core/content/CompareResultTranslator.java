package com.enonic.wem.core.content;

import com.enonic.wem.api.content.CompareContentResult;
import com.enonic.wem.api.content.CompareContentResults;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.entity.NodeComparison;
import com.enonic.wem.api.entity.NodeComparisons;

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
        return new CompareContentResult( nodeComparison.getCompareStatus(), ContentId.from( nodeComparison.getEntityId() ) );
    }

}
