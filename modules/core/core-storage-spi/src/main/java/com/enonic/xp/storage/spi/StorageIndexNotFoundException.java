package com.enonic.xp.storage.spi;

/**
 * The storage backend has no index/table for the given repository. Backend
 * implementations translate their native not-found signal (e.g. Elasticsearch's
 * {@code IndexNotFoundException}) into this type at the SPI boundary.
 */
public class StorageIndexNotFoundException
    extends RuntimeException
{
    public StorageIndexNotFoundException( final String message )
    {
        super( message );
    }

    public StorageIndexNotFoundException( final String message, final Throwable cause )
    {
        super( message, cause );
    }
}
