package com.enonic.wem.api.module;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

import com.google.common.base.Preconditions;

import static com.enonic.wem.api.module.Module.newModule;

public final class ModuleImporter
{
    public Module importModuleFromZip( final Path zipFile )
    {
        return null;
    }

    public Module importModuleFromDirectory( final Path moduleDirectoryPath )
        throws IOException
    {
        if ( !Files.exists( moduleDirectoryPath ) )
        {
            throw new NoSuchFileException( moduleDirectoryPath.toString() );
        }
        Preconditions.checkArgument( Files.isDirectory( moduleDirectoryPath ), "Module file is not a directory: " + moduleDirectoryPath );
        final Path xmlFile = moduleDirectoryPath.resolve( Module.MODULE_XML );
        Preconditions.checkArgument( Files.isRegularFile( xmlFile ), "Module descriptor file not found: " + xmlFile );

        final String moduleDirName = moduleDirectoryPath.getFileName().toString();
        final ModuleKey moduleKey = ModuleKey.from( moduleDirName );
        final Module.Builder moduleBuilder = newModule().moduleKey( moduleKey );

        readModuleXml( moduleBuilder, xmlFile );
        final ModuleFileEntry.Builder rootEntry = moduleBuilder.getModuleDirectoryEntry();

        importFiles( rootEntry, moduleDirectoryPath, true );

        return moduleBuilder.build();
    }

    private void importFiles( final ModuleFileEntry.Builder parentEntry, final Path parentDirectory, final boolean rootLevel )
        throws IOException
    {
        try (DirectoryStream<Path> ds = Files.newDirectoryStream( parentDirectory ))
        {
            for ( Path path : ds )
            {
                if ( rootLevel && Module.MODULE_XML.equals( path.getFileName().toString() ) )
                {
                    continue;
                }
                if ( Files.isDirectory( path ) )
                {
                    final ModuleFileEntry.Builder directoryEntry = ModuleFileEntry.directoryBuilder( path.getFileName().toString() );
                    importFiles( directoryEntry, path, false );
                    parentEntry.addEntry( directoryEntry );
                }
                else
                {
                    parentEntry.addFile( path );
                }
            }
        }
    }

    private void readModuleXml( final Module.Builder moduleBuilder, final Path xmlFile )
    {

    }
}
