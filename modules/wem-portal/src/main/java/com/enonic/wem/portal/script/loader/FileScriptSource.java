package com.enonic.wem.portal.script.loader;

import java.io.File;
import java.io.IOException;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

final class FileScriptSource
    extends ScriptSource
{
    private final File file;

    public FileScriptSource( final String name, final File file )
    {
        super( name );
        this.file = file;
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
