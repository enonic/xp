package com.enonic.xp.repo.impl.elasticsearch.search;

import org.elasticsearch.client.Client;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.repo.impl.elasticsearch.executor.SearchExecutor;
import com.enonic.xp.repo.impl.search.SearchDao;
import com.enonic.xp.repo.impl.search.SearchRequest;
import com.enonic.xp.repo.impl.search.result.SearchResult;

@Component
public class SearchDaoImpl
    implements SearchDao
{
    private Client client;

    @Override
    public SearchResult search( final SearchRequest searchRequest )
    {
        return SearchExecutor.create( this.client ).
            build().
            execute( searchRequest );
    }

    @Reference
    public void setClient( final Client client )
    {
        this.client = client;
    }
}
