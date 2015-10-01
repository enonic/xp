package com.enonic.xp.repo.impl.elasticsearch.result;

import java.util.Map;

import org.elasticsearch.search.SearchHitField;

import com.enonic.xp.repo.impl.ReturnValues;

class ReturnValuesFactory
{
    public static ReturnValues create( final org.elasticsearch.search.SearchHit hit )
    {
        final Map<String, SearchHitField> fields = hit.getFields();

        final ReturnValues.Builder builder = ReturnValues.create();

        for ( final String fieldName : fields.keySet() )
        {
            final SearchHitField hitField = fields.get( fieldName );
            builder.add( fieldName, hitField.values() );
        }

        return builder.build();
    }
}
