package com.enonic.xp.admin.impl.rest.resource.security.json;


import java.util.ArrayList;
import java.util.List;

public final class SyncIdProvidersResultJson
{
    private final List<SyncIdProviderResultJson> results;

    public SyncIdProvidersResultJson()
    {
        this.results = new ArrayList<>();
    }

    public void add( final SyncIdProviderResultJson synchResult )
    {
        this.results.add( synchResult );
    }

    public List<SyncIdProviderResultJson> getResults()
    {
        return results;
    }
}
