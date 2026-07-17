package com.enonic.xp.storage.spi;

/**
 * Read-consistency preference for {@link NodeStore} get operations. Mirrors the two-value
 * {@code _local}/{@code _primary} search preference already used by core-repo
 * ({@code com.enonic.xp.repo.impl.SearchPreference}), duplicated here so the SPI does not
 * depend on core-repo.
 */
public enum SearchPreference
{
    LOCAL, PRIMARY
}
