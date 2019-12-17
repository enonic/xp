package com.enonic.xp.repo.impl.elasticsearch;

import org.apache.commons.lang.StringUtils;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;

import static com.google.common.base.Strings.isNullOrEmpty;

public class OrderbyValueResolver
{

    private static final int ORDER_BY_STRING_MAX_LENGHT = 1024;

    public static String getOrderbyValue( Value value )
    {
        if ( value == null )
        {
            return null;
        }

        if ( value.isNumericType() )
        {
            return getNumericOrderBy( value );
        }

        if ( value.isDateType() )
        {
            return getOrderbyValueForDate( value );
        }

        return getOrderbyValueForString( value.toString() );
    }

    private static String getNumericOrderBy( Value value )
    {

        if ( value.getType().equals( ValueTypes.DOUBLE ) )
        {
            return LexiSortable.toLexiSortable( value.asDouble() );
        }

        if ( value.getType().equals( ValueTypes.LONG ) )
        {
            return LexiSortable.toLexiSortable( value.asLong() );
        }

        throw new IllegalArgumentException( "Not able to create numeric sortable value for " + value.getType() );
    }

    private static String getOrderbyValueForDate( Value value )
    {
        return IndexFormats.FULL_DATE_FORMAT.format( value.asInstant() );
    }

    private static String getOrderbyValueForString( String value )
    {
        if ( isNullOrEmpty( value ) )
        {
            return "";
        }

        return StringUtils.lowerCase( StringUtils.substring( value, 0, Math.min( value.length(), ORDER_BY_STRING_MAX_LENGHT ) ) );
    }


}
