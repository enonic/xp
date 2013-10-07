package com.enonic.wem.api.module;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

public final class ModuleExporter
{
    private final static Map<String, String> ZIP_FS_ENV = ImmutableMap.of( "create", "true" );

    public void exportModuleToZip( final Module module, final Path targetDirectory )
        throws IOException, URISyntaxException
    {
        final Path zipLocation = targetDirectory.resolve( module.getModuleKey().toString() + ".zip" );
        if ( Files.exists( zipLocation ) )
        {
            throw new FileAlreadyExistsException( zipLocation.toString() );
        }

        final URI fileUri = zipLocation.toUri();
        final URI zipUri = new URI( "jar:" + fileUri.getScheme(), fileUri.getPath(), null );

        final ModuleFileEntry parentEntry = module.getModuleDirectoryEntry();
        try (FileSystem zipFs = FileSystems.newFileSystem( zipUri, ZIP_FS_ENV ))
        {
            final Path rootPath = zipFs.getPath( "/" );
            exportFiles( parentEntry, rootPath );
            writeModuleXml( module, rootPath.resolve( Module.MODULE_XML ) );
        }
    }

    public void exportModuleToDirectory( final Module module, final Path directory )
    {

    }

    private void exportFiles( final ModuleFileEntry parentEntry, final Path parentDirectory )
        throws IOException
    {
        for ( ModuleFileEntry fileEntry : parentEntry )
        {
            final Path path = parentDirectory.resolve( fileEntry.getName() );
            if ( fileEntry.isFile() )
            {
                try (InputStream is = fileEntry.getResource().getByteSource().openStream())
                {
                    Files.copy( is, path );
                }
            }
            else
            {
                Files.createDirectory( path );
                exportFiles( fileEntry, path );
            }
        }
    }

    private void writeModuleXml( final Module module, final Path xmlFile )
    {

    }
}
