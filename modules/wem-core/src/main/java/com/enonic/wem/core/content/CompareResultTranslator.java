package com.enonic.wem.core.content;

import com.enonic.wem.api.content.CompareContentResult;
import com.enonic.wem.api.content.CompareContentResults;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.entity.EntityComparison;
import com.enonic.wem.api.entity.EntityComparisons;

class CompareResultTranslator
{
    public static CompareContentResults translate( final EntityComparisons entityComparisons )
    {
        final CompareContentResults.Builder builder = CompareContentResults.create();

        for ( final EntityComparison entityComparison : entityComparisons )
        {
            builder.add( doTranslate( entityComparison ) );
        }

        return builder.build();
    }

    public static CompareContentResult translate( final EntityComparison entityComparison )
    {
        return doTranslate( entityComparison );
    }

    private static CompareContentResult doTranslate( final EntityComparison entityComparison )
    {
        return new CompareContentResult( entityComparison.getCompareStatus(), ContentId.from( entityComparison.getEntityId() ) );
    }

}
