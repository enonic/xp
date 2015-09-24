package com.enonic.wem.repo.internal.elasticsearch.search;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.wem.repo.internal.elasticsearch.ElasticsearchDao;
import com.enonic.wem.repo.internal.elasticsearch.query.ElasticsearchQuery;
import com.enonic.wem.repo.internal.elasticsearch.query.translator.ElasticsearchQueryTranslator;
import com.enonic.wem.repo.internal.search.SearchDao;
import com.enonic.wem.repo.internal.search.SearchRequest;
import com.enonic.wem.repo.internal.storage.result.SearchHits;
import com.enonic.wem.repo.internal.storage.result.SearchResult;
import com.enonic.xp.node.SearchMode;

@Component
public class ElasticsearchSearchDao
    implements SearchDao
{
    private ElasticsearchDao elasticsearchDao;

    @Override
    public SearchResult search( final SearchRequest searchRequest )
    {
        final ElasticsearchQuery esQuery = ElasticsearchQueryTranslator.translate( searchRequest );

        if ( searchRequest.getQuery().getSearchMode().equals( SearchMode.COUNT ) )
        {
            final long count = elasticsearchDao.count( esQuery );

            return SearchResult.create().
                hits( SearchHits.create().
                    totalHits( count ).
                    build() ).
                build();
        }

        return this.elasticsearchDao.search( esQuery );
    }

    @Reference
    public void setElasticsearchDao( final ElasticsearchDao elasticsearchDao )
    {
        this.elasticsearchDao = elasticsearchDao;
    }
}
