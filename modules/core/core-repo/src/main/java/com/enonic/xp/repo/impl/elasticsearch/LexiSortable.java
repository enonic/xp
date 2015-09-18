package com.enonic.xp.repo.impl.elasticsearch;

public final class LexiSortable
{
    private final static char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private static final int LEXI_STRING_LEN = 17;

    private static final long SIGN_MASK = 0x8000000000000000L;

    private static String toHexString( final char type, long val )
    {
        final char[] buf = new char[LEXI_STRING_LEN];
        int charPos = LEXI_STRING_LEN;

        do
        {
            buf[--charPos] = HEX_DIGITS[(int) ( val & 0xf )];
            val >>>= 4;
        }
        while ( charPos > 0 );

        buf[0] = type;
        return new String( buf );
    }


    public static String toLexiSortable( final long l )
    {
        return toHexString( 'l', l ^ SIGN_MASK );
    }

    public static String toLexiSortable( final double d )
    {
        long tmp = Double.doubleToRawLongBits( d );
        return toHexString( 'd', ( tmp < 0 ) ? ~tmp : ( tmp ^ SIGN_MASK ) );
    }
}
