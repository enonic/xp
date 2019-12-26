package com.enonic.xp.util;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class HexEncoder
{
    private final static char[] HEX = "0123456789abcdef".toCharArray();

    public static String toHex( final byte[] value )
    {
        char[] buffer = new char[value.length * 2];
        for ( int i = 0; i < value.length; i++ )
        {
            buffer[2 * i] = HEX[( value[i] >> 4 ) & 0x0f];
            buffer[2 * i + 1] = HEX[value[i] & 0x0f];
        }

        return new String( buffer );
    }

    public static byte[] fromHex( String value )
    {
        byte[] binary = new byte[value.length() / 2];
        for ( int i = 0; i < binary.length; i++ )
        {
            binary[i] = (byte) Integer.parseInt( value.substring( 2 * i, 2 * i + 2 ), 16 );
        }
        return binary;
    }
}
