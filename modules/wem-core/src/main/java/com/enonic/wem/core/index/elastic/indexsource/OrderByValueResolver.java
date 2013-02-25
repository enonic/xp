package com.enonic.wem.core.index.elastic.indexsource;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.util.NumericUtils;

import com.enonic.wem.core.index.elastic.ElasticsearchFormatter;

public class OrderByValueResolver
{
    public static String getOrderbyValue( Object value )
    {
        return doGetOrderByValue( value );
    }

    private static String doGetOrderByValue( final Object value )
    {
        if ( value == null )
        {
            return null;
        }

        if ( value instanceof Object[] )
        {

            if ( ( (Object[]) value ).length >= 1 )
            {
                return doGetOrderByValue( ( (Object[]) value )[0] );
            }
            else
            {
                return null;
            }
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

            return doubleToPrefixCoded( (Double) value );
        }

        if ( value instanceof Float )
        {
            return floatToPrefixCoded( (Float) value );
        }

        if ( value instanceof Long )
        {
            return longToPrefixCoded( (Long) value );
        }

        return intToPrefixCoded( value.intValue() );
    }

    private static String getOrderbyValueForDate( Date value )
    {
        return ElasticsearchFormatter.formatDateAsStringFull( value );
    }

    private static String getOrderbyValueForString( String value )
    {
        return StringUtils.lowerCase( value );
    }


    /**
     * Implemented convenience-methods existing in Lucene 3.0.0, but removed in 3.6.0 *
     */

    private static String doubleToPrefixCoded( double val )
    {
        return longToPrefixCoded( NumericUtils.doubleToSortableLong( val ) );
    }

    private static String floatToPrefixCoded( float val )
    {
        return intToPrefixCoded( NumericUtils.floatToSortableInt( val ) );
    }

    private static String longToPrefixCoded( final long val )
    {
        return longToPrefixCoded( val, 0 );
    }

    private static String longToPrefixCoded( final long val, final int shift )
    {
        final char[] buffer = new char[NumericUtils.BUF_SIZE_LONG];
        final int len = longToPrefixCoded( val, shift, buffer );
        return new String( buffer, 0, len );
    }

    private static int longToPrefixCoded( final long val, final int shift, final char[] buffer )
    {
        if ( shift > 63 || shift < 0 )
        {
            throw new IllegalArgumentException( "Illegal shift value, must be 0..63" );
        }
        int nChars = ( 63 - shift ) / 7 + 1, len = nChars + 1;
        buffer[0] = (char) ( NumericUtils.SHIFT_START_LONG + shift );
        long sortableBits = val ^ 0x8000000000000000L;
        sortableBits >>>= shift;
        while ( nChars >= 1 )
        {
            // Store 7 bits per character for good efficiency when UTF-8 encoding.
            // The whole number is right-justified so that lucene can prefix-encode
            // the terms more efficiently.
            buffer[nChars--] = (char) ( sortableBits & 0x7f );
            sortableBits >>>= 7;
        }
        return len;
    }

    private static String intToPrefixCoded( final int val )
    {
        return intToPrefixCoded( val, 0 );
    }

    private static String intToPrefixCoded( final int val, final int shift )
    {
        final char[] buffer = new char[NumericUtils.BUF_SIZE_INT];
        final int len = intToPrefixCoded( val, shift, buffer );
        return new String( buffer, 0, len );
    }

    private static int intToPrefixCoded( final int val, final int shift, final char[] buffer )
    {
        if ( shift > 31 || shift < 0 )
        {
            throw new IllegalArgumentException( "Illegal shift value, must be 0..31" );
        }
        int nChars = ( 31 - shift ) / 7 + 1, len = nChars + 1;
        buffer[0] = (char) ( NumericUtils.SHIFT_START_INT + shift );
        int sortableBits = val ^ 0x80000000;
        sortableBits >>>= shift;
        while ( nChars >= 1 )
        {
            // Store 7 bits per character for good efficiency when UTF-8 encoding.
            // The whole number is right-justified so that lucene can prefix-encode
            // the terms more efficiently.
            buffer[nChars--] = (char) ( sortableBits & 0x7f );
            sortableBits >>>= 7;
        }
        return len;
    }
}
