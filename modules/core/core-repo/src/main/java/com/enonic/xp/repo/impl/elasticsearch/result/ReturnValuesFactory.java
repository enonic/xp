package com.enonic.xp.repo.impl.elasticsearch.result;

import java.util.Map;

import org.elasticsearch.common.document.DocumentField;

import com.enonic.xp.repo.impl.ReturnValues;

class ReturnValuesFactory
{
    public static ReturnValues create( final org.elasticsearch.search.SearchHit hit )
    {
        final Map<String, DocumentField> fields = hit.getFields();

        final ReturnValues.Builder builder = ReturnValues.create();

        for ( final String fieldName : fields.keySet() )
        {
            final DocumentField hitField = fields.get( fieldName );
            builder.add( fieldName, hitField.getValues() );
        }

        return builder.build();
    }
}
