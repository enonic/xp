package com.enonic.xp.admin.impl.rest.resource.security.json;


import java.util.ArrayList;
import java.util.List;

public final class SyncUserStoresResultJson
{
    private final List<SyncUserStoreResultJson> results;

    public SyncUserStoresResultJson()
    {
        this.results = new ArrayList<>();
    }

    public void add( final SyncUserStoreResultJson synchResult )
    {
        this.results.add( synchResult );
    }

    public List<SyncUserStoreResultJson> getResults()
    {
        return results;
    }
}
