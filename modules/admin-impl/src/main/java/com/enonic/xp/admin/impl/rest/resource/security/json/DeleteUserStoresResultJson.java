package com.enonic.xp.admin.impl.rest.resource.security.json;


import java.util.ArrayList;
import java.util.List;

public final class DeleteUserStoresResultJson
{
    private final List<DeleteUserStoreResultJson> results;

    public DeleteUserStoresResultJson()
    {
        this.results = new ArrayList<>();
    }

    public void add( final DeleteUserStoreResultJson deleteResult )
    {
        this.results.add( deleteResult );
    }

    public List<DeleteUserStoreResultJson> getResults()
    {
        return results;
    }
}
