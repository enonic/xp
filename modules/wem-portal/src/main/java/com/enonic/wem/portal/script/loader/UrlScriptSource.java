package com.enonic.wem.portal.script.loader;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import com.enonic.wem.api.module.ModuleResourceKey;

final class UrlScriptSource
    extends ScriptSourceBase
{
    private final String name;

    private final URL url;

    public UrlScriptSource( final String name, final URL url )
    {
        this.name = name;
        this.url = url;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public ModuleResourceKey getResourceKey()
    {
        return null;
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
