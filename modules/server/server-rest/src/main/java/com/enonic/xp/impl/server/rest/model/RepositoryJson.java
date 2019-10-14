package com.enonic.xp.impl.server.rest.model;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.Repository;

public final class RepositoryJson
{
    public String id;

    public List<String> branches;

    public static RepositoryJson create( final Repository repository )
    {
        final RepositoryJson json = new RepositoryJson();

        json.id = repository.getId().toString();
        json.branches = new ArrayList<>();
        for ( final Branch branch : repository.getBranches() )
        {
            json.branches.add( branch.getValue() );
        }

        return json;
    }
}
