package com.enonic.wem.repo.internal.storage;

import java.util.Collection;

import com.enonic.wem.repo.internal.storage.result.GetResult;
import com.enonic.wem.repo.internal.storage.result.ReturnValues;

public class CacheHelper
{

    public static GetResult createGetResult( final CacheResult cacheResult, final ReturnFields returnFields )
    {
        if ( !cacheResult.exists() )
        {
            return GetResult.empty();
        }

        final ReturnValues.Builder builder = ReturnValues.create();

        for ( final ReturnField field : returnFields )
        {
            final StorageData data = cacheResult.getStorageData();

            final Collection<Object> values = data.get( field.getPath() );

            if ( values == null || values.isEmpty() )
            {
                throw new RuntimeException( "Expected data with path '" + field.getPath() + " in result" );
            }

            builder.add( field.getPath(), values ).build();
        }

        return GetResult.create().
            id( cacheResult.getId() ).
            resultFieldValues( builder.build() ).
            build();


    }

}
