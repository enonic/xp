package com.enonic.xp.internal.blobstore.file;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSource;
import com.google.common.io.MoreFiles;
import com.google.common.io.RecursiveDeleteOption;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.BlobStoreException;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.blob.SegmentLevel;

public final class FileBlobStore
    implements BlobStore
{
    private static final Logger LOG = LoggerFactory.getLogger( FileBlobStore.class );

    private final Path baseDir;

    public FileBlobStore( final Path baseDir )
    {
        this.baseDir = baseDir;
        try
        {
            Files.createDirectories( baseDir );
        }
        catch ( IOException e )
        {
            throw new BlobStoreException( "Failed to create directory [" + baseDir + "]", e );
        }
    }

    @Override
    public BlobRecord getRecord( final Segment segment, final BlobKey key )
        throws BlobStoreException
    {
        final Path file = resolveBlobPath( segment, key );

        if ( !Files.exists( file ) )
        {
            return null;
        }

        return new FileBlobRecord( key, file );
    }

    @Override
    public BlobRecord addRecord( final Segment segment, final ByteSource in )
        throws BlobStoreException
    {
        try
        {
            return addRecord( segment, BlobKey.from( in ), in );
        }
        catch ( final IOException e )
        {
            throw new BlobStoreException( "Failed to add blob", e );
        }
    }

    @Override
    public BlobRecord addRecord( final Segment segment, final BlobRecord record )
        throws BlobStoreException
    {
        try
        {
            return addRecord( segment, record.getKey(), record.getBytes() );
        }
        catch ( IOException e )
        {
            throw new BlobStoreException( "Failed to add blob", e );
        }
    }

    @Override
    public void removeRecord( final Segment segment, final BlobKey key )
        throws BlobStoreException
    {
        final Path file = resolveBlobPath( segment, key );
        try
        {
            Files.deleteIfExists( file );
        }
        catch ( IOException e )
        {
            throw new BlobStoreException( "Failed to remove blob", e );
        }
    }

    @Override
    public Stream<BlobRecord> list( final Segment segment )
    {
        final Path segmentPath = resolveSegmentPath( segment );
        try
        {
            return Files.find( segmentPath, 4, ( path, attr ) -> attr.isRegularFile() && path.getFileName().toString().length() >= 6 )
                .map( path -> new FileBlobRecord( BlobKey.from( path.getFileName().toString() ), path ) );
        }
        catch ( IOException e )
        {
            throw new BlobStoreException( "Failed to list files", e );
        }
    }

    @Override
    public Stream<Segment> listSegments()
    {
        try (Stream<Path> pathStream = Files.find( baseDir, 2,
                                                   ( path, attr ) -> attr.isDirectory() && baseDir.relativize( path ).getNameCount() == 2 ))
        {
            return pathStream.map( path -> Segment.from( path.getParent().getFileName().toString(), path.getFileName().toString() ) )
                .collect( Collectors.toUnmodifiableList() )
                .stream();
        }
        catch ( IOException e )
        {
            throw new BlobStoreException( "Failed to list segments", e );
        }
    }

    @Override
    public void deleteSegment( final Segment segment )
    {
        try
        {
            final Path segmentParentDirectory = baseDir.resolve( segment.getLevel( 0 ).getValue() );
            final Path segmentDirectory = segmentParentDirectory.resolve( segment.getLevel( 1 ).getValue() );

            try
            {
                MoreFiles.deleteRecursively( segmentDirectory, RecursiveDeleteOption.ALLOW_INSECURE );
            }
            catch ( NoSuchFileException e )
            {
                LOG.debug( "No such file [{}]. Skipping delete.", segmentDirectory );
            }

            try
            {
                Files.delete( segmentParentDirectory );
            }
            catch ( DirectoryNotEmptyException e )
            {
                LOG.debug( "Segment parent directory [{}] is not empty. Skipping delete.", segmentParentDirectory );
            }

        }
        catch ( IOException e )
        {
            throw new BlobStoreException( "Failed to delete segment", e );
        }
    }

    private BlobRecord addRecord( final Segment segment, final BlobKey key, final ByteSource in )
        throws IOException
    {
        final Path file = resolveBlobPath( segment, key );

        if ( !Files.exists( file ) )
        {
            Files.createDirectories( file.getParent() );
            try (var inStream = in.openStream())
            {
                Files.copy( inStream, file );
            }
            catch ( FileAlreadyExistsException e )
            {
                LOG.debug( "File already exists [{}]", file, e );
            }
        }

        return new FileBlobRecord( key, file );
    }

    private Path resolveBlobPath( final Segment segment, final BlobKey key )
    {
        final String id = key.toString();
        return resolveSegmentPath( segment ).resolve( id.substring( 0, 2 ) )
            .resolve( id.substring( 2, 4 ) )
            .resolve( id.substring( 4, 6 ) )
            .resolve( id );
    }

    private Path resolveSegmentPath( final Segment segment )
    {
        Path file = baseDir;

        for ( SegmentLevel level : segment.getLevels() )
        {
            file = file.resolve( level.getValue() );
        }
        return file;
    }
}
