package com.enonic.wem.portal.script.loader;

import java.io.File;
import java.io.IOException;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import com.enonic.wem.api.module.ModuleResourceKey;

final class ModuleScriptSource
    extends ScriptSourceBase
{
    private final ModuleResourceKey key;

    private final File file;

    public ModuleScriptSource( final ModuleResourceKey key, final File file )
    {
        this.key = key;
        this.file = file;
    }

    @Override
    public String getName()
    {
        return this.key.toString();
    }

    @Override
    public ModuleResourceKey getResourceKey()
    {
        return this.key;
    }

    @Override
    public String getLocation()
    {
        return this.file.toString();
    }

    @Override
    protected String readSource()
        throws IOException
    {
        return Files.toString( this.file, Charsets.UTF_8 );
    }

    @Override
    public long getTimestamp()
    {
        return this.file.lastModified();
    }
}
