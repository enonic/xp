package com.enonic.xp.admin.impl.rest.resource.security.json;


import java.util.ArrayList;
import java.util.List;

public final class DeleteIdProvidersResultJson
{
    private final List<DeleteIdProviderResultJson> results;

    public DeleteIdProvidersResultJson()
    {
        this.results = new ArrayList<>();
    }

    public void add( final DeleteIdProviderResultJson deleteResult )
    {
        this.results.add( deleteResult );
    }

    public List<DeleteIdProviderResultJson> getResults()
    {
        return results;
    }
}
