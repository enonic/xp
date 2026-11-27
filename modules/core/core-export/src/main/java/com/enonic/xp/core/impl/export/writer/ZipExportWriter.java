package com.enonic.xp.core.impl.export.writer;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileAlreadyExistsException;
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

    private static final String PARTIAL_FILE_EXTENSION = ".partial";

    private final ZipArchiveOutputStream zipArchiveOutputStream;

    private final String exportName;

    private final Path baseDirectory;

    private final Path partialFile;

    private final Path targetFile;

    private boolean completed;

    private boolean closed;

    private ZipExportWriter( final String exportName, final Path baseDirectory, final ZipArchiveOutputStream zipArchiveOutputStream,
                             final Path partialFile, final Path targetFile )
    {
        this.exportName = exportName;
        this.baseDirectory = baseDirectory.normalize();
        this.zipArchiveOutputStream = zipArchiveOutputStream;
        this.partialFile = partialFile;
        this.targetFile = targetFile;
    }

    public static ZipExportWriter create( final Path basePath, final String exportName )
    {
        Preconditions.checkArgument( FileNames.isSafeFileName( exportName ) );
        final Path targetFile = basePath.resolve( exportName + ZIP_FILE_EXTENSION );
        final Path partialFile = basePath.resolve( exportName + ZIP_FILE_EXTENSION + PARTIAL_FILE_EXTENSION );

        if ( Files.exists( targetFile ) )
        {
            throw new ExportNodeException( "Export '" + exportName + "' already exists" );
        }

        try
        {
            Files.createDirectories( basePath );
            final Path baseDirectory = basePath.resolve( exportName );
            final ZipArchiveOutputStream zipArchiveOutputStream = new ZipArchiveOutputStream(
                Files.newOutputStream( partialFile, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE ) );

            return new ZipExportWriter( exportName, baseDirectory, zipArchiveOutputStream, partialFile, targetFile );
        }
        catch ( FileAlreadyExistsException e )
        {
            throw new ExportNodeException( "Export '" + exportName + "' is already in progress or a previous one was interrupted: " +
                                               partialFile.getFileName() + " exists", e );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    @Override
    public void writeElement( final Path itemPath, final String export )
    {
        final String entryPath = resolveEntryPath( itemPath );

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
        final String entryPath = resolveEntryPath( itemPath );

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

    private String resolveEntryPath( final Path itemPath )
    {
        final Path normalizedItem = itemPath.toAbsolutePath().normalize();
        final Path normalizedBase = baseDirectory.toAbsolutePath().normalize();

        if ( !normalizedItem.startsWith( normalizedBase ) )
        {
            throw new ExportNodeException( "Item path " + itemPath + " is not within export base " + baseDirectory );
        }

        final Path relativePath = normalizedBase.relativize( normalizedItem );

        if ( relativePath.getNameCount() == 0 )
        {
            return exportName;
        }

        final StringBuilder sb = new StringBuilder( exportName );
        for ( Path component : relativePath )
        {
            sb.append( '/' ).append( component );
        }
        return sb.toString();
    }

    /**
     * Marks the export as complete. On {@link #close()} a completed export is published under its final name,
     * otherwise the partially written archive is deleted.
     */
    public void complete()
    {
        this.completed = true;
    }

    @Override
    public void close()
        throws IOException
    {
        if ( closed )
        {
            return;
        }
        closed = true;

        try
        {
            zipArchiveOutputStream.close();
            if ( completed )
            {
                Files.move( partialFile, targetFile );
            }
            else
            {
                Files.deleteIfExists( partialFile );
            }
        }
        catch ( IOException | RuntimeException e )
        {
            try
            {
                Files.deleteIfExists( partialFile );
            }
            catch ( IOException suppressed )
            {
                e.addSuppressed( suppressed );
            }
            throw e;
        }
    }
}
