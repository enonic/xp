package com.enonic.xp.repo.impl.dump;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSource;
import com.google.common.io.Files;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.BlobStoreException;
import com.enonic.xp.blob.Segment;

public class DumpBlobStore
    implements BlobStore
{
    private final static Logger LOG = LoggerFactory.getLogger( DumpBlobStore.class );

    private final File baseDir;

    public DumpBlobStore( final File baseDir )
    {
        this.baseDir = baseDir;
        mkdirs( this.baseDir, true );
    }

    @Override
    public BlobRecord getRecord( final Segment segment, final BlobKey key )
        throws BlobStoreException
    {
        final File file = getBlobFile( segment, key );
        if ( !file.exists() )
        {
            return null;
        }

        return new DumpBlobRecord( key, file );
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
        throw new UnsupportedOperationException( "Not implemented" );
    }

    private BlobRecord addRecord( final Segment segment, final BlobKey key, final ByteSource in )
        throws IOException
    {
        final File file = getBlobFile( segment, key );
        if ( !file.exists() )
        {
            mkdirs( file.getParentFile(), false );
            in.copyTo( Files.asByteSink( file ) );
        }

        return new DumpBlobRecord( key, file );
    }

    @Override
    public Stream<BlobRecord> list( final Segment segment )
    {
        throw new BlobStoreException( "Not implemented" );
    }

    private File getBlobFile( final Segment segment, final BlobKey key )
    {
        final String id = key.toString();
        File file = this.baseDir;
        file = new File( file, segment.getValue() );
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
