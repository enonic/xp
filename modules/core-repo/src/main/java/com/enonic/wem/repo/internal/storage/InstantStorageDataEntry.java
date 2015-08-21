package com.enonic.wem.repo.internal.storage;

import java.time.Instant;

public class InstantStorageDataEntry
    extends AbstractStorageDataEntry<Instant>
{
    public InstantStorageDataEntry( final String key, final Instant value )
    {
        super( key, value );
    }
}
