package com.enonic.xp.repo.impl.blob.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import com.enonic.xp.repo.impl.blob.BlobKey;
import com.enonic.xp.repo.impl.blob.BlobRecord;
import com.enonic.xp.repo.impl.blob.BlobStoreException;

final class FileBlobRecord
    extends BlobRecord
{
    private final File file;

    public FileBlobRecord( final BlobKey key, final File file )
    {
        super( key );
        this.file = file;
    }

    @Override
    public long getLength()
    {
        return this.file.length();
    }

    public File getAsFile()
    {
        return file;
    }

    @Override
    public InputStream getStream()
        throws BlobStoreException
    {
        try
        {
            return new FileInputStream( this.file );
        }
        catch ( FileNotFoundException e )
        {
            throw new BlobStoreException( "Could not find blob [" + getKey().toString() + "]", e );
        }
    }
}
