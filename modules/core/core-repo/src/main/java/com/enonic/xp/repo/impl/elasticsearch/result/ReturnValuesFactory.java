package com.enonic.xp.repo.impl.elasticsearch.result;

import java.util.Map;

import com.enonic.xp.repo.impl.ReturnValues;

class ReturnValuesFactory
{
    public static ReturnValues create( final org.elasticsearch.search.SearchHit hit )
    {
        final Map<String, Object> fields = hit.getSourceAsMap();

        final ReturnValues.Builder builder = ReturnValues.create();

        for ( final String fieldName : fields.keySet() )
        {
            final Object hitField = fields.get( fieldName );
            builder.add( fieldName, hitField );
        }

        return builder.build();
    }
}
