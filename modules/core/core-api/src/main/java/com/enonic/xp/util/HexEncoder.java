package com.enonic.xp.util;

import java.util.HexFormat;

@Deprecated
public class HexEncoder
{
    public static String toHex( final byte[] value )
    {
        return HexFormat.of().formatHex( value );
    }

    public static byte[] fromHex( final String value )
    {
        return HexFormat.of().parseHex( value );
    }
}
