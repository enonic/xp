package com.enonic.xp.vfs;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;

import com.enonic.xp.util.Exceptions;

final class LocalFile
    implements VirtualFile
{
    private final VirtualFilePath virtualFilePath;

    private final Path path;

    public LocalFile( final Path path )
    {
        this.virtualFilePath = VirtualFilePaths.from( path );
        this.path = path;
    }

    @Override
    public String getName()
    {
        return this.path.getFileName().toString();
    }

    @Override
    public VirtualFilePath getPath()
    {
        return this.virtualFilePath;
    }

    @Override
    public URL getUrl()
    {
        try
        {
            return this.path.toUri().toURL();
        }
        catch ( MalformedURLException e )
        {
            throw Exceptions.unchecked( e );
        }
    }

    @Override
    public boolean isFolder()
    {
        return Files.isDirectory( this.path );
    }

    @Override
    public boolean isFile()
    {
        return Files.isRegularFile( this.path );
    }

    @Override
    public List<VirtualFile> getChildren()
    {
        if ( !isFolder() )
        {
            return new ArrayList<>();
        }

        final List<VirtualFile> virtualFiles = new ArrayList<>();

        try (final Stream<Path> list = Files.list( this.path ))
        {
            list.forEach( ( path ) -> virtualFiles.add( VirtualFiles.from( path ) ) );
        }
        catch ( final IOException e )
        {
            throw Exceptions.unchecked( e );
        }

        return virtualFiles;
    }

    @Override
    public CharSource getCharSource()
    {
        if ( !isFile() )
        {
            return null;
        }

        return com.google.common.io.Files.asCharSource( this.path.toFile(), StandardCharsets.UTF_8 );
    }

    @Override
    public ByteSource getByteSource()
    {
        if ( !isFile() )
        {
            return null;
        }

        return com.google.common.io.Files.asByteSource( this.path.toFile() );
    }

    @Override
    public boolean exists()
    {
        return path.toFile().exists();
    }

    @Override
    public VirtualFile resolve( final VirtualFilePath virtualFilePath )
    {
        final Path localPath = virtualFilePath.toLocalPath();

        return VirtualFiles.from( this.path.resolve( localPath ) );
    }

}
