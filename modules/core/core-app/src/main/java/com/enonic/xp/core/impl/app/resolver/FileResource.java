package com.enonic.xp.core.impl.app.resolver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

import org.jspecify.annotations.Nullable;

import com.google.common.io.ByteSource;
import com.google.common.io.MoreFiles;

import com.enonic.xp.resource.ResourceBase;
import com.enonic.xp.resource.ResourceKey;

/**
 * A resource served from a file on disk: the sources of an application in development. The file changes under the resource
 * without any signal, so its modification time, at the precision the file system keeps it, is the revision of its content.
 */
public final class FileResource
    extends ResourceBase
    implements RevisionedResource
{
    private final Path path;

    public FileResource( final ResourceKey key, final Path path )
    {
        super( key );
        this.path = path;
    }

    @Override
    public boolean exists()
    {
        return Files.isRegularFile( path );
    }

    @Override
    public long getSize()
    {
        try
        {
            return Files.size( path );
        }
        catch ( final IOException e )
        {
            return -1;
        }
    }

    @Override
    public long getTimestamp()
    {
        final FileTime lastModified = lastModified();
        return lastModified == null ? -1 : lastModified.toMillis();
    }

    @Override
    public ByteSource getBytes()
    {
        return MoreFiles.asByteSource( path );
    }

    @Override
    public String getResolverName()
    {
        return "file";
    }

    @Override
    public @Nullable Object revision()
    {
        return lastModified();
    }

    private @Nullable FileTime lastModified()
    {
        try
        {
            return Files.getLastModifiedTime( path );
        }
        catch ( final IOException e )
        {
            return null;
        }
    }
}
