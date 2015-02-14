package com.enonic.wem.repo.internal.blob;

import java.io.InputStream;

import com.enonic.xp.blob.BlobKey;

public abstract class BlobRecord
    implements Blob
{
    private final BlobKey key;

    public BlobRecord( final BlobKey key )
    {
        this.key = key;
    }

    public final BlobKey getKey()
    {
        return this.key;
    }

    public abstract long getLength();

    public abstract InputStream getStream()
        throws BlobStoreException;

    public final String toString()
    {
        return this.key.toString();
    }

    public final int hashCode()
    {
        return this.key.hashCode();
    }

    public final boolean equals( final Object object )
    {
        return ( object instanceof BlobRecord ) && this.key.equals( ( (BlobRecord) object ).key );
    }
}
