package com.enonic.wem.core.exporters;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleFileEntry;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.core.module.ModuleXmlSerializer;

@XMLFilename("module.xml")
public class ModuleExporter
    extends AbstractEntityExporter<Module>
{
    public static final String MODULE_XML = ModuleExporter.class.getAnnotation( XMLFilename.class ).value();

    private final ModuleXmlSerializer xmlSerializer = new ModuleXmlSerializer();

    @Override
    public void exportObject( final Module object, final Path rootPath )
        throws IOException
    {
        super.exportObject( object, rootPath );

        exportFiles( object.getModuleDirectoryEntry(), rootPath );
    }

    @Override
    protected String toXMLString( final Module module )
    {
        return xmlSerializer.toString( module );
    }

    @Override
    protected Module fromXMLString( final String xml, final Path directoryPath )
        throws IOException
    {
        final ModuleKey moduleKey = ModuleKey.from( resolveId( directoryPath ) );
        final Module.Builder moduleBuilder = Module.newModule().moduleKey( moduleKey );

        xmlSerializer.toModule( xml, moduleBuilder );

        final ModuleFileEntry.Builder rootEntry = moduleBuilder.getModuleDirectoryEntry();
        importFiles( rootEntry, directoryPath, true );

        return moduleBuilder.build();
    }

    private void exportFiles( final Object parentEntry, final Path parentDirectory )
        throws IOException
    {
        for ( final ModuleFileEntry fileEntry : (ModuleFileEntry) parentEntry )
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

    private void importFiles( final ModuleFileEntry.Builder parentEntry, final Path parentDirectory, final boolean rootLevel )
        throws IOException
    {
        final String FS_SEPARATOR = parentDirectory.getFileSystem().getSeparator();

        try (final DirectoryStream<Path> ds = Files.newDirectoryStream( parentDirectory ))
        {
            for ( final Path path : ds )
            {
                if ( rootLevel && MODULE_XML.equals( path.getFileName().toString() ) )
                {
                    continue;
                }
                if ( Files.isDirectory( path ) )
                {
                    final String dirName = path.getFileName().toString().replace( FS_SEPARATOR, "" );
                    final ModuleFileEntry.Builder directoryEntry = ModuleFileEntry.directoryBuilder( dirName );
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
}
