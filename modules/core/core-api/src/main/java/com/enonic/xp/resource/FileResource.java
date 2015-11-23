package com.enonic.xp.resource;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.common.annotations.Beta;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;

@Beta
public final class FileResource
    extends ResourceBase
{
    private final File file;

    public FileResource( final ResourceKey key, final File file )
    {
        super( key );
        this.file = file;
    }

    @Override
    public URL getUrl()
    {
        try
        {
            return this.file.toURI().toURL();
        }
        catch ( final MalformedURLException e )
        {
            return null;
        }
    }

    @Override
    public boolean exists()
    {
        return this.file.exists();
    }

    @Override
    public long getSize()
    {
        return this.file.exists() ? this.file.length() : -1;
    }

    @Override
    public long getTimestamp()
    {
        return this.file.exists() ? this.file.lastModified() : -1;
    }

    @Override
    public ByteSource getBytes()
    {
        requireExists();
        return Files.asByteSource( this.file );
    }
}
