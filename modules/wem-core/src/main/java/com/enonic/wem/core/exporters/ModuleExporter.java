package com.enonic.wem.core.exporters;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleFileEntry;
import com.enonic.wem.core.module.ModuleXmlSerializer;

@XMLFilename("module.xml")
final public class ModuleExporter
    extends EntityExporter<Module>
{
    private final ModuleXmlSerializer xmlSerializer = new ModuleXmlSerializer();

    protected String serializeToXMLString( final Module module )
    {
        return xmlSerializer.toString( module );
    }

    protected ModuleFileEntry getSerializingObject( final Module module )
    {
        return module.getModuleDirectoryEntry();
    }

    protected void exportFiles( final Object parentEntry, final Path parentDirectory )
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
}
