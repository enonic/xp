package com.enonic.wem.core.module;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.inject.Inject;

import com.google.common.io.ByteSource;

import com.enonic.wem.api.command.module.CreateModuleResource;
import com.enonic.wem.api.module.ModuleNotFoundException;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.core.command.CommandHandler;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class CreateModuleResourceHandler
    extends CommandHandler<CreateModuleResource>
{
    private ModuleResourcePathResolver moduleResourcePathResolver;

    @Override
    public void handle()
        throws Exception
    {
        final ModuleResourceKey moduleResourceKey = command.getResourceKey();

        final Path modulePath = moduleResourcePathResolver.resolveModulePath( moduleResourceKey.getModuleKey() );
        if ( !Files.isDirectory( modulePath ) )
        {
            throw new ModuleNotFoundException( moduleResourceKey.getModuleKey() );
        }

        final Path resourceFilePath = moduleResourcePathResolver.resolveResourcePath( moduleResourceKey );

        Files.createDirectories( resourceFilePath.getParent() );
        final ByteSource byteSource = command.getResource().getByteSource();
        try (InputStream is = byteSource.openStream())
        {
            Files.copy( is, resourceFilePath, REPLACE_EXISTING );
        }
        command.setResult( command.getResource() );
    }

    @Inject
    public void setModuleResourcePathResolver( final ModuleResourcePathResolver moduleResourcePathResolver )
    {
        this.moduleResourcePathResolver = moduleResourcePathResolver;
    }
}
