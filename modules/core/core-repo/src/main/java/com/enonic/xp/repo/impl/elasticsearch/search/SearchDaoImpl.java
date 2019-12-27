package com.enonic.xp.repo.impl.elasticsearch.search;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.elasticsearch.client.impl.EsClient;
import com.enonic.xp.repo.impl.elasticsearch.executor.SearchExecutor;
import com.enonic.xp.repo.impl.search.SearchDao;
import com.enonic.xp.repo.impl.search.SearchRequest;
import com.enonic.xp.repo.impl.search.result.SearchResult;

@Component
public class SearchDaoImpl
    implements SearchDao
{
    private EsClient client;

    @Override
    public SearchResult search( final SearchRequest searchRequest )
    {
        return SearchExecutor.create( this.client ).
            build().
            execute( searchRequest );
    }

    @Reference
    public void setClient( final EsClient client )
    {
        this.client = client;
    }
}
