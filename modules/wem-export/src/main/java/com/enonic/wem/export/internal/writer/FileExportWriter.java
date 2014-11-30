package com.enonic.wem.export.internal.writer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.enonic.wem.export.ExportNodeException;

public class FileExportWriter
    implements ExportWriter
{

    @Override
    public void createDirectory( final ExportItemPath path )
    {
        doCreateDirectories( path );
    }

    private void doCreateDirectories( final ExportItemPath path )
    {
        try
        {
            Files.createDirectories( Paths.get( path.getPathAsString() ) );
        }
        catch ( IOException e )
        {
            throw new ExportNodeException( "failed to create directory with path " + path.getPathAsString() );
        }
    }

    @Override
    public void writeElement( final ExportItemPath itemPath, final String export )
    {
        this.doCreateDirectories( itemPath.removeLastElement() );

        try
        {
            final Path exportFile = Files.createFile( itemPath.getPath() );

            Files.write( exportFile, export.getBytes() );
        }
        catch ( IOException e )
        {
            throw new ExportNodeException( "failed to create file with path " + itemPath.getPathAsString(), e );
        }
    }
}
