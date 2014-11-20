package com.enonic.wem.admin.rest.resource.security.json;


import java.util.ArrayList;
import java.util.List;

public final class DeletePrincipalsResultJson
{
    private final List<DeletePrincipalResultJson> results;

    public DeletePrincipalsResultJson()
    {
        this.results = new ArrayList<>();
    }

    public void add( final DeletePrincipalResultJson deleteResult )
    {
        this.results.add( deleteResult );
    }

    public List<DeletePrincipalResultJson> getResults()
    {
        return results;
    }
}
