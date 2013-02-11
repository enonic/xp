package com.enonic.wem.core.search.elastic.indexsource;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.nodep.lucene.util.NumericUtils;

import com.enonic.wem.core.search.elastic.ElasticsearchFormatter;

public class IndexSourceOrderbyValueResolver
{
    public static String getOrderbyValue( Object value )
    {
        if ( value == null )
        {
            return null;
        }

        if ( value instanceof Number )
        {
            return getNumericOrderBy( (Number) value );
        }

        if ( value instanceof Date )
        {
            return getOrderbyValueForDate( (Date) value );
        }

        return getOrderbyValueForString( value.toString() );
    }


    private static String getNumericOrderBy( Number value )
    {

        if ( value instanceof Double )
        {
            return NumericUtils.doubleToPrefixCoded( (Double) value );
        }

        if ( value instanceof Float )
        {
            return NumericUtils.floatToPrefixCoded( (Float) value );
        }

        if ( value instanceof Long )
        {
            return NumericUtils.longToPrefixCoded( (Long) value );
        }

        return NumericUtils.intToPrefixCoded( value.intValue() );
    }

    private static String getOrderbyValueForDate( Date value )
    {
        return ElasticsearchFormatter.formatDateAsStringFull( value );
    }

    private static String getOrderbyValueForString( String value )
    {
        return StringUtils.lowerCase( value );
    }

}
