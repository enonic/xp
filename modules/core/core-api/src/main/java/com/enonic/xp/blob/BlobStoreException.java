package com.enonic.xp.blob;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
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
