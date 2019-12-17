package com.enonic.xp.repo.impl.elasticsearch.result;

import java.util.Map;

import org.elasticsearch.action.get.GetResponse;

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

        final ReturnValues.Builder builder = ReturnValues.create();

        final Map<String, Object> sourceAsMap = getResponse.getSourceAsMap();

        for ( final Map.Entry<String, Object> filed : sourceAsMap.entrySet() )
        {
            builder.add( filed.getKey(), filed.getValue() );
        }

        return GetResult.create().
            id( getResponse.getId() ).
            resultFieldValues( builder.build() ).
            build();
    }


}
