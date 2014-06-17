package com.enonic.wem.core.content;

import com.enonic.wem.api.content.ContentCompareResult;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.entity.EntityComparison;

class CompareResultTranslator
{

    public static ContentCompareResult translate( final EntityComparison entityComparison )
    {
        return new ContentCompareResult( entityComparison.getCompareState(), ContentId.from( entityComparison.getEntityId() ) );
    }

}
