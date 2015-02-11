package com.enonic.wem.api.vfs;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;

import com.enonic.wem.api.util.Exceptions;

final class LocalFile
    implements VirtualFile
{
    private final VirtualFilePath virtualFilePath;

    private final Path path;

    public LocalFile( final Path path )
    {
        this.virtualFilePath = VirtualFilePath.from( path );
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
            return Lists.newArrayList();
        }

        final List<VirtualFile> virtualFiles = Lists.newArrayList();

        try
        {
            final Stream<Path> list = Files.list( this.path );

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

        return com.google.common.io.Files.asCharSource( this.path.toFile(), Charsets.UTF_8 );
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
    public VirtualFile resolve( final String path )
    {
        return VirtualFiles.from( this.path.resolve( path ) );
    }

}
