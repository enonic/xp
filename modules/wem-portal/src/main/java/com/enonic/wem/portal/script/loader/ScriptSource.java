package com.enonic.wem.portal.script.loader;

import java.io.IOException;

import com.google.common.base.Throwables;

public abstract class ScriptSource
{
    private final String name;

    public ScriptSource( final String name )
    {
        this.name = name;
    }

    public final String getName()
    {
        return this.name;
    }

    public abstract String getLocation();

    public final String getCacheKey()
    {
        return getLocation() + "_" + getTimestamp();
    }

    public final String getScriptAsString()
    {
        try
        {
            return readSource();
        }
        catch ( final IOException e )
        {
            throw Throwables.propagate( e );
        }
    }

    protected abstract String readSource()
        throws IOException;

    public abstract long getTimestamp();

    @Override
    public final String toString()
    {
        return this.name;
    }
}
