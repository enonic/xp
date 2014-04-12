package com.enonic.wem.portal.script.loader;

import java.io.IOException;
import java.nio.file.Path;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.io.Files;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.resource.ResourceKey;

public class ScriptSourceImpl
    implements ScriptSource
{
    private final Path path;

    private final ResourceKey key;

    public ScriptSourceImpl( final ResourceKey key, final Path path )
    {
        this.path = path;
        this.key = key;
    }

    @Override
    public String getName()
    {
        return this.key.toString();
    }

    @Override
    public Path getPath()
    {
        return this.path;
    }

    @Override
    public String getScriptAsString()
    {
        try
        {
            return Files.toString( this.path.toFile(), Charsets.UTF_8 );
        }
        catch ( final IOException e )
        {
            throw Throwables.propagate( e );
        }
    }

    @Override
    public long getTimestamp()
    {
        return this.path.toFile().lastModified();
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
