package com.enonic.wem.portal.script.loader;

import java.io.IOException;

public abstract class ScriptSource
{
    private final String name;

    private String source;

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
        throws IOException
    {
        if ( this.source == null )
        {
            this.source = readSource();
        }

        return this.source;
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
