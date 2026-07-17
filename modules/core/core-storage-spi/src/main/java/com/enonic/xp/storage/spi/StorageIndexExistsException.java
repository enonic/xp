package com.enonic.xp.storage.spi;

/**
 * The storage backend already has an index/table for the given repository. Backend
 * implementations translate their native already-exists signal (e.g. Elasticsearch's
 * {@code IndexAlreadyExistsException}) into this type at the SPI boundary.
 */
public class StorageIndexExistsException
    extends RuntimeException
{
    public StorageIndexExistsException( final String message )
    {
        super( message );
    }

    public StorageIndexExistsException( final String message, final Throwable cause )
    {
        super( message, cause );
    }
}
