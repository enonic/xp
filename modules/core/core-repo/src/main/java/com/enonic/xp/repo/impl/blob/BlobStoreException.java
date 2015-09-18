package com.enonic.xp.repo.impl.blob;

public final class BlobStoreException
    extends RuntimeException
{
    public BlobStoreException( final String message )
    {
        super( message );
    }

    public BlobStoreException( final String message, final Throwable cause )
    {
        super( message, cause );
    }
}
