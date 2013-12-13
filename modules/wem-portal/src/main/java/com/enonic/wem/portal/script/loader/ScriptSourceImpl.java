package com.enonic.wem.portal.script.loader;

import java.io.IOException;
import java.net.URI;
import java.net.URLConnection;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.io.Resources;

import com.enonic.wem.api.module.ModuleKey;

final class ScriptSourceImpl
    implements ScriptSource
{
    private final String name;

    private final URI uri;

    private final ModuleKey moduleKey;

    public ScriptSourceImpl( final String name, final URI uri, final ModuleKey moduleKey )
    {
        this.name = name;
        this.uri = uri;
        this.moduleKey = moduleKey;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public URI getUri()
    {
        return this.uri;
    }

    @Override
    public String getScriptAsString()
    {
        try
        {
            return Resources.toString( this.uri.toURL(), Charsets.UTF_8 );
        }
        catch ( final IOException e )
        {
            throw Throwables.propagate( e );
        }
    }

    @Override
    public long getTimestamp()
    {
        try
        {
            final URLConnection conn = this.uri.toURL().openConnection();
            return conn.getLastModified();
        }
        catch ( final Exception e )
        {
            return 0;
        }
    }

    @Override
    public ModuleKey getModule()
    {
        return this.moduleKey;
    }

    @Override
    public boolean isFromSystem()
    {
        return this.moduleKey == null;
    }

    @Override
    public boolean isFromModule()
    {
        return this.moduleKey != null;
    }

    @Override
    public String toString()
    {
        return this.name;
    }
}
