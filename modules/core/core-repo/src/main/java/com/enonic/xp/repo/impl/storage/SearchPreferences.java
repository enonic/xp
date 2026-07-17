package com.enonic.xp.repo.impl.storage;

import com.enonic.xp.repo.impl.SearchPreference;

/**
 * Maps between core-repo's {@link SearchPreference} and the SPI's
 * {@link com.enonic.xp.storage.spi.SearchPreference}, so both sides of the {@code NodeStore}
 * boundary (the ES-free service layer and the ES {@code NodeStore} implementation) can
 * speak their own type.
 */
public final class SearchPreferences
{
    private SearchPreferences()
    {
    }

    public static com.enonic.xp.storage.spi.SearchPreference toSpi( final SearchPreference searchPreference )
    {
        if ( searchPreference == null )
        {
            return null;
        }
        return switch ( searchPreference )
        {
            case LOCAL -> com.enonic.xp.storage.spi.SearchPreference.LOCAL;
            case PRIMARY -> com.enonic.xp.storage.spi.SearchPreference.PRIMARY;
        };
    }

    public static SearchPreference fromSpi( final com.enonic.xp.storage.spi.SearchPreference searchPreference )
    {
        if ( searchPreference == null )
        {
            return null;
        }
        return switch ( searchPreference )
        {
            case LOCAL -> SearchPreference.LOCAL;
            case PRIMARY -> SearchPreference.PRIMARY;
        };
    }
}
