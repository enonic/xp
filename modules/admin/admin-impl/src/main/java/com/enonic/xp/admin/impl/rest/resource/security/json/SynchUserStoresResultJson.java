package com.enonic.xp.admin.impl.rest.resource.security.json;


import java.util.ArrayList;
import java.util.List;

public final class SynchUserStoresResultJson
{
    private final List<SynchUserStoreResultJson> results;

    public SynchUserStoresResultJson()
    {
        this.results = new ArrayList<>();
    }

    public void add( final SynchUserStoreResultJson synchResult )
    {
        this.results.add( synchResult );
    }

    public List<SynchUserStoreResultJson> getResults()
    {
        return results;
    }
}
