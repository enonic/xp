package com.enonic.wem.core.module;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;

import com.enonic.wem.api.module.ModuleNotFoundException;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.module.ResourcePath;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceNotFoundException;
import com.enonic.wem.util.Exceptions;

final class GetModuleResourceCommand
{
    private ModuleResourcePathResolver moduleResourcePathResolver;

    private ModuleResourceKey key;

    public Resource execute()
    {
        Preconditions.checkNotNull( this.key, "resourceKey cannot be null" );

        try
        {
            return doExecute();
        }
        catch ( IOException e )
        {
            throw Exceptions.newRutime( "Error retrieving module resource [{0}]", this.key ).withCause( e );
        }
    }

    private Resource doExecute()
        throws IOException
    {
        final Path modulePath = moduleResourcePathResolver.resolveModulePath( this.key.getModuleKey() );
        if ( !Files.isDirectory( modulePath ) )
        {
            throw new ModuleNotFoundException( this.key.getModuleKey() );
        }

        final ResourcePath resourcePath = this.key.getPath();
        final Path resourceFileSystemPath = moduleResourcePathResolver.resolveResourcePath( this.key );
        if ( !Files.isRegularFile( resourceFileSystemPath ) )
        {
            throw new ResourceNotFoundException( resourcePath );
        }

        final ByteSource byteSource = com.google.common.io.Files.asByteSource( resourceFileSystemPath.toFile() );
        final Resource resource = Resource.newResource().
            name( resourcePath.getName() ).
            byteSource( byteSource ).
            size( Files.size( resourceFileSystemPath ) ).
            build();
        return resource;
    }

    public GetModuleResourceCommand moduleResourcePathResolver( final ModuleResourcePathResolver moduleResourcePathResolver )
    {
        this.moduleResourcePathResolver = moduleResourcePathResolver;
        return this;
    }

    public GetModuleResourceCommand key( final ModuleResourceKey key )
    {
        this.key = key;
        return this;
    }
}
