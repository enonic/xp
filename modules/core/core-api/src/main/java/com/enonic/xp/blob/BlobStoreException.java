package com.enonic.xp.blob;

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
