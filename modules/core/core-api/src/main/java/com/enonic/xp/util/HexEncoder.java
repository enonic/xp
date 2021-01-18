package com.enonic.xp.util;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.core.internal.HexCoder;

@PublicApi
public class HexEncoder
{
    public static String toHex( final byte[] value )
    {
        return HexCoder.toHex( value );
    }

    public static byte[] fromHex( final String value )
    {
        return HexCoder.fromHex( value );
    }
}
