package com.enonic.xp.testing;

import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public final class TestHelper
{
    public static String load( final String path )
        throws Exception
    {
        final URL url = TestHelper.class.getResource( path );
        if ( url == null )
        {
            return null;
        }

        return Resources.toString( url, Charsets.UTF_8 );
    }
}
