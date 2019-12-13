package com.enonic.xp.impl.server.rest.model;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.repository.Repositories;
import com.enonic.xp.repository.Repository;

public final class RepositoriesJson
{
    public List<RepositoryJson> repositories;

    public static RepositoriesJson create( final Repositories repositories )
    {
        final RepositoriesJson json = new RepositoriesJson();

        json.repositories = new ArrayList<>();
        for ( final Repository repo : repositories )
        {
            json.repositories.add( RepositoryJson.create( repo ) );
        }

        return json;
    }
}
