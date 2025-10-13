package com.enonic.xp.repo.impl.elasticsearch.document.indexitem;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.data.Value;
import com.enonic.xp.index.IndexPath;

class BaseTypeFactory
{
    public static List<IndexItem> create( final IndexPath indexPath, final Value value )
    {
        final ImmutableList.Builder<IndexItem> baseTypes = ImmutableList.builder();

        if ( value.isDateType() )
        {
            baseTypes.add( new IndexItemInstant( indexPath, value.asInstant() ) );
        }
        else if ( value.isNumericType() )
        {
            baseTypes.add( new IndexItemDouble( indexPath, value.asDouble() ) );
        }
        else if ( value.isGeoPoint() )
        {
            baseTypes.add( new IndexItemGeoPoint( indexPath, value.asGeoPoint() ) );
        }

        baseTypes.add( new IndexItemString( indexPath, value.asString() ) );

        return baseTypes.build();
    }
}
