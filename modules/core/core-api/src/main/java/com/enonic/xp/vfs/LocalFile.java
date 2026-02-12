package com.enonic.xp.vfs;

import java.io.IOException;
import java.io.UncheckedIOException;
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
import com.google.common.io.MoreFiles;

final class LocalFile
    implements VirtualFile
{
    private final VirtualFilePath virtualFilePath;

    private final Path path;

    LocalFile( final Path path )
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
            throw new UncheckedIOException( e );
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

        try (Stream<Path> list = Files.list( this.path ))
        {
            list.forEach( ( path ) -> virtualFiles.add( VirtualFiles.from( path ) ) );
        }
        catch ( final IOException e )
        {
            throw new UncheckedIOException( e );
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

        return MoreFiles.asCharSource( this.path, StandardCharsets.UTF_8 );
    }

    @Override
    public ByteSource getByteSource()
    {
        if ( !isFile() )
        {
            return null;
        }

        return MoreFiles.asByteSource( this.path );
    }

    @Override
    public boolean exists()
    {
        return Files.exists( path );
    }

    @Override
    public VirtualFile resolve( final VirtualFilePath virtualFilePath )
    {
        final Path localPath = virtualFilePath.toLocalPath();

        return VirtualFiles.from( this.path.resolve( localPath ) );
    }

}
