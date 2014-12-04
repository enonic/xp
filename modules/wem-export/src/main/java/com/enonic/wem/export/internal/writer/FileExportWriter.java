package com.enonic.wem.export.internal.writer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.enonic.wem.api.export.ExportNodeException;

public class FileExportWriter
    implements ExportWriter
{
    @Override
    public void createDirectory( final Path path )
    {
        doCreateDirectories( path );
    }

    private void doCreateDirectories( final Path path )
    {
        try
        {
            Files.createDirectories( path );
        }
        catch ( IOException e )
        {
            throw new ExportNodeException( "failed to create directory with path " + path.toString() );
        }
    }

    @Override
    public void writeElement( final Path itemPath, final String export )
    {
        this.doCreateDirectories( itemPath.getParent() );

        try
        {
            final Path exportFile = Files.createFile( itemPath );

            Files.write( exportFile, export.getBytes() );
        }
        catch ( IOException e )
        {
            throw new ExportNodeException( "failed to create file with path " + itemPath.toString(), e );
        }
    }
}
