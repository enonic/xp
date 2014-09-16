package com.enonic.wem.core.index.document;

final class IndexNumericUtils
{
    public static final byte SHIFT_START_LONG = 0x20;

    public static final int BUF_SIZE_LONG = 63 / 7 + 2;

    public static final byte SHIFT_START_INT = 0x60;

    public static final int BUF_SIZE_INT = 31 / 7 + 2;

    public static long doubleToSortableLong( double val )
    {
        return sortableDoubleBits( Double.doubleToLongBits( val ) );
    }

    public static int floatToSortableInt( float val )
    {
        return sortableFloatBits( Float.floatToIntBits( val ) );
    }

    /**
     * Converts IEEE 754 representation of a double to sortable order (or back to the original)
     */
    public static long sortableDoubleBits( long bits )
    {
        return bits ^ ( bits >> 63 ) & 0x7fffffffffffffffL;
    }

    /**
     * Converts IEEE 754 representation of a float to sortable order (or back to the original)
     */
    public static int sortableFloatBits( int bits )
    {
        return bits ^ ( bits >> 31 ) & 0x7fffffff;
    }
}
