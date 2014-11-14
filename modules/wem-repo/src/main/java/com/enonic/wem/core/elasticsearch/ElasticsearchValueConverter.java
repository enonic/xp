package com.enonic.wem.core.elasticsearch;

import java.sql.Date;

import com.enonic.wem.api.data.Value;

public class ElasticsearchValueConverter
{
    public static Object convert( final Value value )
    {
        if ( value.isDateType() )
        {
            return Date.from( value.asInstant() );
        }
        else if ( value.isNumericType() )
        {
            return value.asDouble();
        }
        else if ( value.isGeoPoint() )
        {
            return value.asString();
        }
        else
        {
            return value.asString();
        }
    }


}
