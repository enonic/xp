package com.enonic.wem.portal.script.runtime;

import java.io.File;
import java.io.IOException;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public final class FileObject
{
    private final File file;

    public FileObject( final File file )
    {
        this.file = file;
    }

    public String asString()
        throws IOException
    {
        return Files.toString( this.file, Charsets.UTF_8 );
    }

    public byte[] asBytes()
        throws IOException
    {
        return Files.toByteArray( this.file );
    }

    @Override
    public String toString()
    {
        return this.file.toString();
    }
}
