package com.enonic.xp.repo.impl.elasticsearch.result;

import java.util.Map;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.common.document.DocumentField;

import com.enonic.xp.repo.impl.ReturnValues;
import com.enonic.xp.repo.impl.storage.GetResult;

public class GetResultFactory
{
    public static GetResult create( final GetResponse getResponse )
    {
        if ( !getResponse.isExists() )
        {
            return GetResult.empty();
        }

        final Map<String, DocumentField> hitFieldMap = getResponse.getFields();

        final ReturnValues.Builder builder = ReturnValues.create();

        for ( final String fieldName : hitFieldMap.keySet() )
        {
            final DocumentField getField = hitFieldMap.get( fieldName );

            builder.add( fieldName, getField.getValues() );
        }

        return GetResult.create().
            id( getResponse.getId() ).
            resultFieldValues( builder.build() ).
            build();
    }


}
