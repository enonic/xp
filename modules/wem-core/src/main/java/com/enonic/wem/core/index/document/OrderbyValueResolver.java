package com.enonic.wem.core.index.document;

import org.apache.commons.lang.StringUtils;

import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.data.type.ValueTypes;
import com.enonic.wem.core.index.IndexFormats;

class OrderbyValueResolver
{

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

        if ( value.getType() == ValueTypes.DOUBLE )
        {
            return doubleToPrefixCoded( value.asDouble() );
        }

        if ( value.getType() == ValueTypes.LONG )
        {
            return longToPrefixCoded( value.asLong() );
        }

        throw new IllegalArgumentException( "Not able to create numeric sortable value for " + value.getType() );
    }

    private static String getOrderbyValueForDate( Value value )
    {
        return IndexFormats.FULL_DATE_FORMAT.format( value.asInstant() );
    }

    private static String getOrderbyValueForString( String value )
    {
        return StringUtils.lowerCase( value );
    }

    private static String doubleToPrefixCoded( double val )
    {
        return longToPrefixCoded( org.apache.lucene.util.NumericUtils.doubleToSortableLong( val ) );
    }

    private static String floatToPrefixCoded( float val )
    {
        return intToPrefixCoded( org.apache.lucene.util.NumericUtils.floatToSortableInt( val ) );
    }

    private static String longToPrefixCoded( final long val )
    {
        return longToPrefixCoded( val, 0 );
    }

    private static String longToPrefixCoded( final long val, final int shift )
    {
        final char[] buffer = new char[org.apache.lucene.util.NumericUtils.BUF_SIZE_LONG];
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
        buffer[0] = (char) ( org.apache.lucene.util.NumericUtils.SHIFT_START_LONG + shift );
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
        final char[] buffer = new char[org.apache.lucene.util.NumericUtils.BUF_SIZE_INT];
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
        buffer[0] = (char) ( org.apache.lucene.util.NumericUtils.SHIFT_START_INT + shift );
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
