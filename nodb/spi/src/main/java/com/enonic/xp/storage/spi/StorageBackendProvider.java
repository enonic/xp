package com.enonic.xp.storage.spi;

/**
 * OSGi service. The runtime selects a provider by name from config
 * (e.g. {@code com.enonic.xp.storage.cfg: backend=nodb}).
 */
public interface StorageBackendProvider
{
    String name();

    StorageBackend create( StorageBackendConfig config );
}
