package com.enonic.wem.internal.blob.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.internal.blob.BlobRecord;
import com.enonic.wem.internal.blob.BlobStoreException;

final class FileBlobRecord
    extends BlobRecord
{
    private final File file;

    public FileBlobRecord( final BlobKey key, final File file )
    {
        super( key );
        this.file = file;
    }

    public long getLength()
    {
        return this.file.length();
    }

    public File getAsFile()
    {
        return file;
    }

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
