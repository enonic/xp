package com.enonic.wem.repo.internal.elasticsearch.result;

import java.util.Map;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.index.get.GetField;

import com.enonic.wem.repo.internal.index.result.GetResult;
import com.enonic.wem.repo.internal.index.result.ResultFieldValues;

public class GetResultFactory
{
    public static GetResult create( final GetResponse getResponse )
    {
        if ( !getResponse.isExists() )
        {
            return GetResult.empty();
        }

        final Map<String, GetField> hitFieldMap = getResponse.getFields();

        final ResultFieldValues.Builder builder = ResultFieldValues.create();

        for ( final String fieldName : hitFieldMap.keySet() )
        {
            final GetField getField = hitFieldMap.get( fieldName );

            builder.add( fieldName, getField.getValues() );
        }

        return GetResult.create().
            id( getResponse.getId() ).
            resultFieldValues( builder.build() ).
            build();
    }


}
