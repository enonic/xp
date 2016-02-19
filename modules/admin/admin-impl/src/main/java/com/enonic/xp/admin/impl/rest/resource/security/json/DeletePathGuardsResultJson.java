package com.enonic.xp.admin.impl.rest.resource.security.json;


import java.util.ArrayList;
import java.util.List;

public final class DeletePathGuardsResultJson
{
    private final List<DeletePathGuardResultJson> results;

    public DeletePathGuardsResultJson()
    {
        this.results = new ArrayList<>();
    }

    public void add( final DeletePathGuardResultJson deleteResult )
    {
        this.results.add( deleteResult );
    }

    public List<DeletePathGuardResultJson> getResults()
    {
        return results;
    }
}
