/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.wem.core.blobstore.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.core.blobstore.BlobKey;
import com.enonic.wem.core.blobstore.BlobKeyCreator;
import com.enonic.wem.core.blobstore.BlobRecord;
import com.enonic.wem.core.blobstore.BlobStore;
import com.enonic.wem.core.blobstore.BlobStoreException;
import com.enonic.wem.core.config.SystemConfig;
import com.enonic.wem.core.lifecycle.LifecycleBean;
import com.enonic.wem.core.lifecycle.RunLevel;

public final class FileBlobStore
    extends LifecycleBean
    implements BlobStore
{
    private final static Logger LOG = LoggerFactory.getLogger( FileBlobStore.class );

    private static final String TMP = "tmp-";

    private final File dir;

    @Inject
    public FileBlobStore( final SystemConfig systemConfig )
    {
        super( RunLevel.L1 );
        this.dir = systemConfig.getBlobStoreDir();
    }

    @Override
    protected void doStart()
        throws Exception
    {
        mkdirs( this.dir, true );
    }

    @Override
    protected void doStop()
        throws Exception
    {
        // Do nothing
    }

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

    public BlobRecord addRecord( final InputStream in )
        throws BlobStoreException
    {
        File tmpFile = null;

        try
        {
            tmpFile = newTemporaryFile();
            final BlobKey key = BlobKeyCreator.createKey( in, new FileOutputStream( tmpFile ) );
            return addRecord( key, tmpFile );
        }
        catch ( IOException e )
        {
            throw new BlobStoreException( "Failed to open tempoary file", e );
        }
        finally
        {
            delete( tmpFile );
        }
    }

    private synchronized BlobRecord addRecord( final BlobKey key, final File tmpFile )
        throws BlobStoreException
    {
        final File file = getBlobFile( key );
        if ( !file.exists() )
        {
            mkdirs( file.getParentFile(), false );
            if ( !tmpFile.renameTo( file ) )
            {
                throw new BlobStoreException( "Failed to rename file [" + key.toString() + "]" );
            }
        }

        return new FileBlobRecord( key, file );
    }

    public Iterable<BlobKey> getAllKeys()
        throws BlobStoreException
    {
        final ArrayList<File> files = new ArrayList<>();
        findFiles( files, this.dir );

        final ArrayList<BlobKey> identifiers = new ArrayList<>();
        for ( final File f : files )
        {
            String name = f.getName();
            if ( !name.startsWith( TMP ) )
            {
                identifiers.add( new BlobKey( name ) );
            }
        }

        return identifiers;
    }

    public boolean deleteRecord( final BlobKey key )
        throws BlobStoreException
    {
        final File file = getBlobFile( key );
        return delete( file );
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

    private void findFiles( final List<File> list, final File file )
    {
        final File[] files = file.listFiles();
        if ( files != null )
        {
            for ( final File f : files )
            {
                if ( f.isDirectory() )
                {
                    findFiles( list, f );
                }
                else
                {
                    list.add( f );
                }
            }
        }
    }

    private File newTemporaryFile()
        throws BlobStoreException
    {
        try
        {
            return File.createTempFile( TMP, null, this.dir );
        }
        catch ( IOException e )
        {
            throw new BlobStoreException( "Failed to create temporary file", e );
        }
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

    private boolean delete( final File file )
    {
        if ( file == null )
        {
            return false;
        }

        if ( !file.exists() )
        {
            return false;
        }

        final boolean flag = file.delete();
        if ( flag )
        {
            LOG.debug( "File deleted [" + file.getAbsolutePath() + "]" );
        }

        return flag;
    }

}
