package com.enonic.wem.core.module;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.common.io.ByteSource;

import com.enonic.wem.api.module.CreateModuleResourceParams;
import com.enonic.wem.api.module.ModuleNotFoundException;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.util.Exceptions;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

final class CreateModuleResourceCommand
{
    private ModuleResourcePathResolver moduleResourcePathResolver;

    private CreateModuleResourceParams params;

    public Resource execute()
    {
        this.params.validate();

        try
        {
            return doExecute();
        }
        catch ( IOException e )
        {
            throw Exceptions.newRutime( "Error creating module resource [{0}]", this.params.getResourceKey() ).withCause( e );
        }
    }

    private Resource doExecute()
        throws IOException
    {
        final ModuleResourceKey moduleResourceKey = this.params.getResourceKey();

        final Path modulePath = moduleResourcePathResolver.resolveModulePath( moduleResourceKey.getModuleKey() );
        if ( !Files.isDirectory( modulePath ) )
        {
            throw new ModuleNotFoundException( moduleResourceKey.getModuleKey() );
        }

        final Path resourceFilePath = moduleResourcePathResolver.resolveResourcePath( moduleResourceKey );

        Files.createDirectories( resourceFilePath.getParent() );
        final ByteSource byteSource = this.params.getResource().getByteSource();
        try (InputStream is = byteSource.openStream())
        {
            Files.copy( is, resourceFilePath, REPLACE_EXISTING );
        }
        return this.params.getResource();
    }

    public CreateModuleResourceCommand moduleResourcePathResolver( final ModuleResourcePathResolver moduleResourcePathResolver )
    {
        this.moduleResourcePathResolver = moduleResourcePathResolver;
        return this;
    }

    public CreateModuleResourceCommand params( final CreateModuleResourceParams params )
    {
        this.params = params;
        return this;
    }
}
