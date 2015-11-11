package com.enonic.xp.repo.impl.blob.file;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSource;
import com.google.common.io.Files;

import com.enonic.xp.repo.impl.blob.BlobKey;
import com.enonic.xp.repo.impl.blob.BlobRecord;
import com.enonic.xp.repo.impl.blob.BlobStore;
import com.enonic.xp.repo.impl.blob.BlobStoreException;

public final class FileBlobStore
    implements BlobStore
{
    private final static Logger LOG = LoggerFactory.getLogger( FileBlobStore.class );

    private final File dir;

    public FileBlobStore( final File dir )
    {
        this.dir = dir;
        mkdirs( this.dir, true );
    }

    @Override
    public BlobRecord getRecord( final BlobKey key )
        throws BlobStoreException
    {
        final File file = getBlobFile( key );
        if ( !file.exists() )
        {
            return null;
        }

        return new FileBlobRecord( key, file );
    }

    @Override
    public BlobRecord addRecord( final ByteSource in )
        throws BlobStoreException
    {
        final BlobKey key = BlobKeyCreator.createKey( in );

        try
        {
            return addRecord( key, in );
        }
        catch ( final IOException e )
        {
            throw new BlobStoreException( "Failed to write blob", e );
        }
    }

    private BlobRecord addRecord( final BlobKey key, final ByteSource in )
        throws IOException
    {
        final File file = getBlobFile( key );
        if ( !file.exists() )
        {
            mkdirs( file.getParentFile(), false );
            in.copyTo( Files.asByteSink( file ) );
        }

        return new FileBlobRecord( key, file );
    }

    private File getBlobFile( final BlobKey key )
    {
        final String id = key.toString();
        File file = this.dir;
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
