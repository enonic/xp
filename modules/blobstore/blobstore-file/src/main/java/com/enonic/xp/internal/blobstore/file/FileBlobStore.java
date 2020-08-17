package com.enonic.xp.internal.blobstore.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.List;
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
        return doGetRecord( segment, key );
    }

    private BlobRecord doGetRecord( final Segment segment, final BlobKey key )
    {
        final Path file = getBlobFile( segment, key );

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
        final BlobKey key = BlobKey.from( in );

        try
        {
            return addRecord( segment, key, in );
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
            return this.addRecord( segment, record.getKey(), record.getBytes() );
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
        final Path file = getBlobFile( segment, key );
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
        try
        {
            return Files.walk( this.baseDir ).
                filter( path -> Files.isRegularFile( path ) ).
                filter( path -> isBlobFileName( segment, path ) ).
                map( path -> {
                    final BlobKey blobKey = BlobKey.from( path.getFileName().toString() );
                    return doGetRecord( segment, blobKey );
                } );
        }
        catch ( IOException e )
        {
            throw new BlobStoreException( "Failed to list files", e );
        }
    }

    @Override
    public Stream<Segment> listSegments()
    {
        try
        {
            return nioFilesList( this.baseDir ).stream().flatMap( firstSegmentLevelFile -> {
                final String firstSegmentLevel = firstSegmentLevelFile.getFileName().toString();

                try
                {
                    return nioFilesList( firstSegmentLevelFile ).stream().
                        map( secondSegmentLevelFile -> {
                            final String secondSegmentLevel = secondSegmentLevelFile.getFileName().toString();
                            return Segment.from( firstSegmentLevel, secondSegmentLevel );
                        } );
                }
                catch ( IOException e )
                {
                    throw new BlobStoreException( "Failed to list segments", e );
                }
            } );
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
            final Path segmentParentDirectory = this.baseDir.resolve( segment.getLevel( 0 ).getValue() );
            final Path segmentDirectory = segmentParentDirectory.resolve( segment.getLevel( 1 ).getValue() );

            if ( Files.exists( segmentDirectory ) )
            {
                MoreFiles.deleteRecursively( segmentDirectory, RecursiveDeleteOption.ALLOW_INSECURE );
            }

            if ( Files.exists( segmentParentDirectory ) && nioFilesList( segmentParentDirectory ).isEmpty() )
            {
                Files.delete( segmentParentDirectory );
            }

        }
        catch ( IOException e )
        {
            throw new BlobStoreException( "Failed to delete segment", e );
        }
    }

    private static List<Path> nioFilesList( Path dir )
        throws IOException
    {
        try (final Stream<Path> stream = Files.list( dir ))
        {
            return stream.collect( Collectors.toList() );
        }
    }

    private BlobRecord addRecord( final Segment segment, final BlobKey key, final ByteSource in )
        throws IOException
    {
        final Path file = getBlobFile( segment, key );

        if ( !Files.exists( file ) )
        {
            Files.createDirectories( file.getParent() );
            in.copyTo( MoreFiles.asByteSink( file ) );
        }
        else
        {
            Files.setLastModifiedTime( file, FileTime.fromMillis( System.currentTimeMillis() ) );
        }

        return new FileBlobRecord( key, file );
    }

    private boolean isBlobFileName( final Segment segment, final Path path )
    {
        final String fileName = path.getFileName().toString();

        if ( fileName.length() < 6 )
        {
            return false;
        }

        return Files.exists( getBlobFile( segment, BlobKey.from( fileName ) ) );
    }

    private Path getBlobFile( final Segment segment, final BlobKey key )
    {
        final String id = key.toString();
        Path file = this.baseDir;

        for ( SegmentLevel level : segment.getLevels() )
        {
            file = file.resolve( level.getValue() );
        }

        return file.
            resolve( id.substring( 0, 2 ) ).
            resolve( id.substring( 2, 4 ) ).
            resolve( id.substring( 4, 6 ) ).
            resolve( id );
    }
}
