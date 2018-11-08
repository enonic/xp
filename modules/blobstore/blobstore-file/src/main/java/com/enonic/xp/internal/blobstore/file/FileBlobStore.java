package com.enonic.xp.internal.blobstore.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSource;
import com.google.common.io.Files;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.BlobStoreException;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.blob.SegmentLevel;

public final class FileBlobStore
    implements BlobStore
{
    private final static Logger LOG = LoggerFactory.getLogger( FileBlobStore.class );

    private final File baseDir;

    public FileBlobStore( final File baseDir )
    {
        this.baseDir = baseDir;
        mkdirs( this.baseDir, true );
    }

    @Override
    public BlobRecord getRecord( final Segment segment, final BlobKey key )
        throws BlobStoreException
    {
        return doGetRecord( segment, key );
    }

    private BlobRecord doGetRecord( final Segment segment, final BlobKey key )
    {
        final File file = getBlobFile( segment, key );
        if ( !file.exists() )
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
        final File file = getBlobFile( segment, key );
        if ( file.exists() )
        {
            if ( !file.delete() )
            {
                throw new BlobStoreException( "Failed to remove blob" );
            }
        }
    }

    @Override
    public Stream<BlobRecord> list( final Segment segment )
    {
        try
        {
            return java.nio.file.Files.walk( this.baseDir.toPath() ).
                filter( path -> path.toFile().isFile() ).
                filter( path -> isBlobFileName( segment, path ) ).
                map( ( path -> {
                    final BlobKey blobKey = BlobKey.from( path.getFileName().toString() );
                    return doGetRecord( segment, blobKey );
                } ) );
        }
        catch ( IOException e )
        {
            throw new BlobStoreException( "Failed to list files", e );
        }
    }

    @Override
    public Stream<Segment> listSegments()
    {
        return Arrays.stream( this.baseDir.listFiles() ).
            flatMap( firstSegmentLevelFile -> {
                final String firstSegmentLevel = firstSegmentLevelFile.getName();

                return Arrays.stream( firstSegmentLevelFile.listFiles() ).
                    map( secondSegmentLevelFile -> {
                        final String secondSegmentLevel = secondSegmentLevelFile.getName();
                        return Segment.from( firstSegmentLevel, secondSegmentLevel );
                    } );
            } );
    }

    @Override
    public void deleteSegment( final Segment segment )
    {
        try
        {
            final File segmentDirectory = this.baseDir.toPath().
                resolve( segment.getLevel( 0 ).getValue() ).
                resolve( segment.getLevel( 1 ).getValue() ).
                toFile();

            if ( segmentDirectory.exists() )
            {
                FileUtils.deleteDirectory( segmentDirectory );
            }
        }
        catch ( IOException e )
        {
            throw new BlobStoreException( "Failed to delete segment", e );
        }
    }

    @SuppressWarnings("unusedReturnValue")
    private BlobRecord addRecord( final Segment segment, final BlobKey key, final ByteSource in )
        throws IOException
    {
        final File file = getBlobFile( segment, key );

        if ( !file.exists() )
        {
            mkdirs( file.getParentFile(), false );
            in.copyTo( Files.asByteSink( file ) );
        }
        else
        {
            file.setLastModified( System.currentTimeMillis() );
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

        return getBlobFile( segment, BlobKey.from( fileName ) ).exists();
    }

    private File getBlobFile( final Segment segment, final BlobKey key )
    {
        final String id = key.toString();
        File file = this.baseDir;
        for ( SegmentLevel level : segment.getLevels() )
        {
            file = new File( file, level.getValue() );
        }
        file = new File( file, id.substring( 0, 2 ) );
        file = new File( file, id.substring( 2, 4 ) );
        file = new File( file, id.substring( 4, 6 ) );
        return new File( file, id );
    }

    private boolean mkdirs( final File dir, final boolean error )
    {
        if ( dir.isDirectory() )
        {
            return false;
        }

        final boolean flag = dir.mkdirs();

        if ( flag )
        {
            LOG.debug( "Created directory [" + dir.getAbsolutePath() + "]" );
        }
        else if ( error )
        {
            throw new BlobStoreException( "Failed to create directory [" + dir.getAbsolutePath() + "]" );
        }

        return flag;
    }
}
