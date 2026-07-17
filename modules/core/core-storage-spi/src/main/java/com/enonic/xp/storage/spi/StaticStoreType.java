package com.enonic.xp.storage.spi;

import java.util.Locale;

/**
 * The three fixed document kinds held in the storage-<repo> index, mirroring core-repo's
 * {@code com.enonic.xp.repo.impl.storage.StaticStorageType} (duplicated here so the SPI does
 * not depend on core-repo). Used only to select a {@link SingleRepoStorageSource} for
 * {@link NodeSearchIndex#search} / the ES {@code SearchDao} — not part of {@link NodeStore},
 * which is already keyed by branch/version/commit operations directly.
 */
public enum StaticStoreType
{
    BRANCH, VERSION, COMMIT;

    private final String name;

    StaticStoreType()
    {
        this.name = this.name().toLowerCase( Locale.ROOT );
    }

    public String getName()
    {
        return this.name;
    }
}
