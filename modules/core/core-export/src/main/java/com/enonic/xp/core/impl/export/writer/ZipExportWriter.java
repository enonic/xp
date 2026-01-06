package com.enonic.xp.core.impl.export.writer;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;

import com.enonic.xp.core.internal.FileNames;
import com.enonic.xp.export.ExportNodeException;

public class ZipExportWriter
    implements ExportWriter
{
    private static final String ZIP_FILE_EXTENSION = ".zip";

    private final ZipArchiveOutputStream zipArchiveOutputStream;

    private final String exportName;

    private ZipExportWriter( final String exportName, final ZipArchiveOutputStream zipArchiveOutputStream )
    {
        this.exportName = exportName;
        this.zipArchiveOutputStream = zipArchiveOutputStream;
    }

    public static ZipExportWriter create( final Path basePath, final String exportName )
    {
        Preconditions.checkArgument( FileNames.isSafeFileName( exportName ) );
        try
        {
            Files.createDirectories( basePath );
            final ZipArchiveOutputStream zipArchiveOutputStream = new ZipArchiveOutputStream(
                Files.newByteChannel( basePath.resolve( exportName + ZIP_FILE_EXTENSION ), StandardOpenOption.CREATE,
                                      StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.TRUNCATE_EXISTING ) );

            return new ZipExportWriter( exportName, zipArchiveOutputStream );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    @Override
    public void writeElement( final Path itemPath, final String export )
    {
        final String entryPath = exportName + "/" + itemPath.toString().replace( '\\', '/' );

        try
        {
            final byte[] data = export.getBytes( java.nio.charset.StandardCharsets.UTF_8 );
            final ZipArchiveEntry archiveEntry = new ZipArchiveEntry( entryPath );
            archiveEntry.setSize( data.length );
            zipArchiveOutputStream.putArchiveEntry( archiveEntry );
            zipArchiveOutputStream.write( data );
            zipArchiveOutputStream.closeArchiveEntry();
        }
        catch ( IOException e )
        {
            throw new ExportNodeException( "failed to write element to zip with path " + itemPath + ": " + e, e );
        }
    }

    @Override
    public void writeSource( final Path itemPath, final ByteSource source )
    {
        final String entryPath = exportName + "/" + itemPath.toString().replace( '\\', '/' );

        try
        {
            final ZipArchiveEntry archiveEntry = new ZipArchiveEntry( entryPath );
            zipArchiveOutputStream.putArchiveEntry( archiveEntry );

            try ( var in = source.openStream() )
            {
                in.transferTo( zipArchiveOutputStream );
            }

            zipArchiveOutputStream.closeArchiveEntry();
        }
        catch ( IOException e )
        {
            throw new ExportNodeException( "failed to write source to zip with path " + itemPath + ": " + e, e );
        }
    }

    @Override
    public void close()
        throws IOException
    {
        zipArchiveOutputStream.close();
    }
}
