package com.enonic.xp.core.impl.export.writer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.common.io.ByteSource;

import com.enonic.xp.export.ExportNodeException;

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
            throw new ExportNodeException( "failed to create directory with path " + path.toString() + ": " + e.toString(), e );
        }
    }

    @Override
    public void writeElement( final Path itemPath, final String export )
    {
        this.doCreateDirectories( itemPath.getParent() );

        try
        {
            Files.writeString( itemPath, export );
        }
        catch ( IOException e )
        {
            throw new ExportNodeException( "failed to create file with path " + itemPath.toString() + ": " + e.toString(), e );
        }
    }

    @Override
    public void writeSource( final Path itemPath, final ByteSource source )
    {
        this.doCreateDirectories( itemPath.getParent() );

        try (final InputStream in = source.openStream())
        {
            Files.copy( in, itemPath );
        }
        catch ( IOException e )
        {
            throw new ExportNodeException( "failed to report source to path " + itemPath.toString() + ": " + e.toString(), e );
        }
    }
}
