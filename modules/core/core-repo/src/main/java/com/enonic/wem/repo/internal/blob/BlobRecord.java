package com.enonic.wem.repo.internal.blob;

import java.io.InputStream;

public abstract class BlobRecord
    implements Blob
{
    private final BlobKey key;

    public BlobRecord( final BlobKey key )
    {
        this.key = key;
    }

    @Override
    public final BlobKey getKey()
    {
        return this.key;
    }

    @Override
    public abstract long getLength();

    @Override
    public abstract InputStream getStream()
        throws BlobStoreException;

    @Override
    public final String toString()
    {
        return this.key.toString();
    }

    @Override
    public final int hashCode()
    {
        return this.key.hashCode();
    }

    @Override
    public final boolean equals( final Object object )
    {
        return ( object instanceof BlobRecord ) && this.key.equals( ( (BlobRecord) object ).key );
    }
}
