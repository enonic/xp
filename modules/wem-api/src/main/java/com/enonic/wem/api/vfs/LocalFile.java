package com.enonic.wem.api.vfs;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import com.google.common.collect.Lists;
import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;

import com.enonic.wem.api.util.Exceptions;

public class LocalFile
    implements VirtualFile
{
    private final Path path;

    public LocalFile( final Path path )
    {
        this.path = path;
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
        final List<VirtualFile> virtualFiles = Lists.newArrayList();

        try
        {
            final Stream<Path> list = Files.list( this.path );

            list.forEach( ( path ) -> virtualFiles.add( VirtualFiles.from( path ) ) );
        }
        catch ( IOException e )
        {
            Exceptions.unchecked( e );
        }

        return virtualFiles;
    }

    @Override
    public CharSource getCharSource()
    {
        final File file = path.toFile();

        if ( !file.exists() )
        {
            return null;
        }

        return com.google.common.io.Files.asCharSource( file, StandardCharsets.UTF_8 );
    }

    @Override
    public ByteSource getByteSource()
    {
        final File file = path.toFile();

        if ( !file.exists() )
        {
            return null;
        }

        return com.google.common.io.Files.asByteSource( file );
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
