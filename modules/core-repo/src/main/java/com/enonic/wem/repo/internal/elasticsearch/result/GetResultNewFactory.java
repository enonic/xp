package com.enonic.wem.repo.internal.elasticsearch.result;

import java.util.Map;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.index.get.GetField;

import com.enonic.wem.repo.internal.index.result.GetResultNew;

public class GetResultNewFactory
{
    public static GetResultNew create( final GetResponse getResponse )
    {
        if ( !getResponse.isExists() )
        {
            return GetResultNew.empty();
        }

        final Map<String, GetField> hitFieldMap = getResponse.getFields();

        final GetResultNew.Builder builder = GetResultNew.create().
            id( getResponse.getId() );

        for ( final String fieldName : hitFieldMap.keySet() )
        {
            final GetField getField = hitFieldMap.get( fieldName );

            builder.add( fieldName, getField.getValues() );
        }

        return builder.build();
    }


}
