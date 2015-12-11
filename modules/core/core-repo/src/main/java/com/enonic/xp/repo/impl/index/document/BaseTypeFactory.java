package com.enonic.xp.repo.impl.index.document;

import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.data.Value;

public class BaseTypeFactory
{
    public static List<IndexItem> create( final String key, final Value value )
    {
        List<IndexItem> baseTypes = Lists.newArrayList();

        if ( value.isDateType() )
        {
            baseTypes.add( new IndexItemInstant( key, value.asInstant() ) );
        }
        else if ( value.isNumericType() )
        {
            baseTypes.add( new IndexItemDouble( key, value.asDouble() ) );
        }
        else if ( value.isGeoPoint() )
        {
            baseTypes.add( new IndexItemGeoPoint( key, value.asGeoPoint() ) );
        }

        baseTypes.add( new IndexItemString( key, value.asString() ) );

        return baseTypes;
    }


}
