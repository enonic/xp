package com.enonic.xp.core.internal;

public final class HexCoder
{
    private static final char[] HEX = "0123456789abcdef".toCharArray();

    private HexCoder()
    {
    }

    public static String toHex( final byte[] value )
    {
        final char[] buffer = new char[value.length * 2];
        for ( int i = 0; i < value.length; i++ )
        {
            final byte v = value[i];
            buffer[2 * i] = HEX[( v >>> 4 ) & 0x0f];
            buffer[2 * i + 1] = HEX[v & 0x0f];
        }
        return new String( buffer );
    }

    public static String toHex( final long value )
    {
        final char[] buffer = new char[Long.BYTES * 2];
        for ( int i = 0; i < Long.BYTES; i++ )
        {
            final byte v = (byte) ( value >>> ( Long.SIZE - ( i + 1 ) * Byte.SIZE ) );
            buffer[2 * i] = HEX[( v >>> 4 ) & 0x0f];
            buffer[2 * i + 1] = HEX[v & 0x0f];
        }
        return new String( buffer );
    }

    public static byte[] fromHex( final CharSequence value )
    {
        int length = value.length();
        if ( length % 2 != 0 )
        {
            throw new IllegalArgumentException( "Invalid hex value" );
        }

        byte[] binary = new byte[length / 2];
        for ( int i = 0; i < length; i += 2 )
        {
            int left = Character.digit( value.charAt( i ), 16 );
            int right = Character.digit( value.charAt( i + 1 ), 16 );
            if ( left == -1 || right == -1 )
            {
                throw new IllegalArgumentException( "Invalid hex value" );
            }
            binary[i / 2] = (byte) ( left << 4 | right );
        }
        return binary;
    }
}
