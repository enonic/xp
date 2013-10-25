package com.enonic.wem.core.module;

import java.nio.file.Files;
import java.nio.file.Path;

import javax.inject.Inject;

import com.google.common.io.ByteSource;

import com.enonic.wem.api.command.module.GetModuleResource;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleNotFoundException;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.module.ResourcePath;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceNotFoundException;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.config.SystemConfig;

public class GetModuleResourceHandler
    extends CommandHandler<GetModuleResource>
{
    private SystemConfig systemConfig;

    @Override
    public void handle()
        throws Exception
    {
        final ModuleResourceKey moduleResourceKey = command.getResourceKey();
        final ModuleKey moduleKey = moduleResourceKey.getModuleKey();

        final Path moduleDirPath = systemConfig.getModuleDir().toPath();
        final Path modulePath = moduleDirPath.resolve( moduleKey.toString() );
        if ( !Files.isDirectory( modulePath ) )
        {
            throw new ModuleNotFoundException( moduleKey );
        }

        final ResourcePath resourcePath = moduleResourceKey.getPath();
        final Path resourceFileSystemPath = modulePath.resolve( resourcePath.toRelativePath().toString() );
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
        command.setResult( resource );
    }

    @Inject
    public void setSystemConfig( final SystemConfig systemConfig )
    {
        this.systemConfig = systemConfig;
    }
}
