package com.enonic.wem.core.module;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleFileEntry;

public final class ModuleExporter
{
    private final static Map<String, String> ZIP_FS_ENV = ImmutableMap.of( "create", "true" );

    private final ModuleXmlSerializer xmlSerializer = new ModuleXmlSerializer();

    public Path exportModuleToZip( final Module module, final Path targetDirectory )
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

        return zipLocation;
    }

    public Path exportModuleToDirectory( final Module module, final Path exportLocation )
        throws IOException
    {
        if ( !Files.isDirectory( exportLocation ) )
        {
            throw new FileNotFoundException( exportLocation.toString() );
        }

        final ModuleFileEntry parentEntry = module.getModuleDirectoryEntry();
        final String directoryName = module.getModuleKey().toString();
        final Path rootPath = exportLocation.resolve( directoryName );
        if ( !Files.isDirectory( rootPath ) )
        {
            Files.createDirectory( rootPath );
        }
        exportFiles( parentEntry, rootPath );
        writeModuleXml( module, rootPath.resolve( Module.MODULE_XML ) );

        return rootPath;
    }

    private void exportFiles( final ModuleFileEntry parentEntry, final Path parentDirectory )
        throws IOException
    {
        for ( ModuleFileEntry fileEntry : parentEntry )
        {
            final Path path = parentDirectory.resolve( fileEntry.getName() );
            if ( !Files.exists( path ) )
            {
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
    }

    private void writeModuleXml( final Module module, final Path xmlFile )
        throws IOException
    {
        final String xml = xmlSerializer.toString( module );
        Files.write( xmlFile, xml.getBytes( Charset.forName( "UTF-8" ) ) );
    }
}
