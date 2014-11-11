package com.enonic.wem.core.elasticsearch;

import com.enonic.wem.core.index.IndexFieldNameNormalizer;

class ReturnField
{
    private final String normalizedReturnFieldName;

    public ReturnField( final String fieldName )
    {
        this.normalizedReturnFieldName = IndexFieldNameNormalizer.normalize( fieldName );
    }

    public String getName()
    {
        return normalizedReturnFieldName;
    }

}
