package com.enonic.xp.repo.impl.elasticsearch.result;

import java.util.Map;

import org.elasticsearch.action.get.GetResponse;

import com.enonic.xp.repo.impl.ReturnFields;
import com.enonic.xp.repo.impl.ReturnValues;
import com.enonic.xp.repo.impl.storage.GetResult;

public class GetResultFactory
{

    public static GetResult create( final GetResponse getResponse, ReturnFields returnFields )
    {
        if ( !getResponse.isExists() )
        {
            return GetResult.empty();
        }

        final Map<String, Object> hitFieldMap = getResponse.getSourceAsMap();

        final ReturnValues.Builder builder = ReturnValues.create();

        for ( String returnFieldName : returnFields.getReturnFieldNames() )
        {
            final Object o = hitFieldMap.get( returnFieldName );
            if (  o != null )
            {
                builder.add( returnFieldName, o );
            }
        }

        return GetResult.create().id( getResponse.getId() ).resultFieldValues( builder.build() ).build();
    }
}
