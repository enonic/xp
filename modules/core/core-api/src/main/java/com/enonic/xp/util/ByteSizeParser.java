package com.enonic.xp.util;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class ByteSizeParser
{

    private ByteSizeParser()
    {
    }

    public static long parse( final String sValue )
    {
        return com.enonic.xp.core.internal.ByteSizeParser.parse( sValue );
    }
}
