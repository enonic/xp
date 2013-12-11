package com.enonic.wem.portal.script.loader;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

final class UrlScriptSource
    extends ScriptSource
{
    private final URL url;

    public UrlScriptSource( final String name, final URL url )
    {
        super( name );
        this.url = url;
    }

    @Override
    public String getLocation()
    {
        return this.url.toString();
    }

    @Override
    protected String readSource()
        throws IOException
    {
        return Resources.toString( this.url, Charsets.UTF_8 );
    }

    @Override
    public long getTimestamp()
    {
        try
        {
            final URLConnection conn = this.url.openConnection();
            return conn.getLastModified();
        }
        catch ( final Exception e )
        {
            return 0;
        }
    }
}
