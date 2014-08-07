package com.enonic.wem.portal.script.loader;

import java.io.IOException;
import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.io.Resources;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceUrlResolver;

public class ScriptSourceImpl
    implements ScriptSource
{
    private final URL url;

    private final ResourceKey key;

    public ScriptSourceImpl( final ResourceKey key )
    {
        this.key = key;
        this.url = ResourceUrlResolver.resolve( key );
    }

    @Override
    public String getName()
    {
        return this.key.toString();
    }

    @Override
    public String getScriptAsString()
    {
        try
        {
            return Resources.toString( this.url, Charsets.UTF_8 );
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
            return this.url.openConnection().getLastModified();
        }
        catch ( IOException e )
        {
            return 0;
        }
    }

    @Override
    public ModuleKey getModule()
    {
        return this.key.getModule();
    }

    @Override
    public ResourceKey getResource()
    {
        return this.key;
    }

    @Override
    public String toString()
    {
        return getName();
    }
}
