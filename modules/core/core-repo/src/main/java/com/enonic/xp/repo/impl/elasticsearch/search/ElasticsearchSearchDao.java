package com.enonic.xp.repo.impl.elasticsearch.search;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.SearchMode;
import com.enonic.xp.query.Query;
import com.enonic.xp.repo.impl.branch.search.NodeBranchQuery;
import com.enonic.xp.repo.impl.elasticsearch.ElasticsearchDao;
import com.enonic.xp.repo.impl.elasticsearch.query.ElasticsearchQuery;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.NodeBranchQueryTranslator;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.NodeQueryTranslator;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.NodeVersionDiffQueryTranslator;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.NodeVersionQueryTranslator;
import com.enonic.xp.repo.impl.search.SearchDao;
import com.enonic.xp.repo.impl.search.SearchRequest;
import com.enonic.xp.repo.impl.search.result.SearchHits;
import com.enonic.xp.repo.impl.search.result.SearchResult;
import com.enonic.xp.repo.impl.version.search.NodeVersionDiffQuery;
import com.enonic.xp.repo.impl.version.search.NodeVersionQuery;

@Component
public class ElasticsearchSearchDao
    implements SearchDao
{
    private ElasticsearchDao elasticsearchDao;

    private final NodeQueryTranslator nodeQueryTranslator = new NodeQueryTranslator();

    private final NodeVersionQueryTranslator nodeVersionQueryTranslator = new NodeVersionQueryTranslator();

    private final NodeVersionDiffQueryTranslator nodeVersionDiffQueryTranslator = new NodeVersionDiffQueryTranslator();

    private final NodeBranchQueryTranslator nodeBranchQueryTranslator = new NodeBranchQueryTranslator();

    @Override
    public SearchResult search( final SearchRequest searchRequest )
    {
        final ElasticsearchQuery esQuery = translateQuery( searchRequest );

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

    private ElasticsearchQuery translateQuery( final SearchRequest searchRequest )
    {
        final Query query = searchRequest.getQuery();

        if ( query instanceof NodeQuery )
        {
            return nodeQueryTranslator.translate( searchRequest );
        }

        if ( query instanceof NodeVersionQuery )
        {
            return nodeVersionQueryTranslator.translate( searchRequest );
        }

        if ( query instanceof NodeVersionDiffQuery )
        {
            return nodeVersionDiffQueryTranslator.translate( searchRequest );
        }

        if ( query instanceof NodeBranchQuery )
        {
            return nodeBranchQueryTranslator.translate( searchRequest );
        }

        throw new UnsupportedOperationException( "Queries of type " + query.getClass() + " not implemented yes" );
    }


    @Reference
    public void setElasticsearchDao( final ElasticsearchDao elasticsearchDao )
    {
        this.elasticsearchDao = elasticsearchDao;
    }
}
