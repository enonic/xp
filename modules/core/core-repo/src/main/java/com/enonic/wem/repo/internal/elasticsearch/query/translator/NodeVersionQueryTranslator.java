package com.enonic.wem.repo.internal.elasticsearch.query.translator;

import org.elasticsearch.index.query.QueryBuilder;

import com.enonic.wem.repo.internal.elasticsearch.query.ElasticsearchQuery;
import com.enonic.wem.repo.internal.search.SearchRequest;
import com.enonic.wem.repo.internal.version.NodeVersionQuery;
import com.enonic.wem.repo.internal.version.VersionIndexPath;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.query.filter.ValueFilter;

class NodeVersionQueryTranslator
    extends AbstractElasticsearchQueryTranslator
{
    static ElasticsearchQuery translate( final SearchRequest searchRequest )
    {
        final NodeVersionQuery nodeVersionQuery = (NodeVersionQuery) searchRequest.getQuery();

        final QueryBuilder queryBuilder = createQueryBuilder( nodeVersionQuery );

        return doCreateEsQuery( nodeVersionQuery, searchRequest, queryBuilder );
    }

    private static QueryBuilder createQueryBuilder( final NodeVersionQuery nodeVersionQuery )
    {
        final QueryBuilderFactory.Builder queryBuilderBuilder = createQuery( nodeVersionQuery );

        addNodeIdFilter( nodeVersionQuery, queryBuilderBuilder );

        return queryBuilderBuilder.build();
    }

    private static void addNodeIdFilter( final NodeVersionQuery nodeVersionQuery, final QueryBuilderFactory.Builder queryBuilderBuilder )
    {
        if ( nodeVersionQuery.getNodeId() != null )
        {
            queryBuilderBuilder.addQueryFilter( ValueFilter.create().
                fieldName( VersionIndexPath.NODE_ID.getPath() ).
                addValue( ValueFactory.newString( nodeVersionQuery.getNodeId().toString() ) ).
                build() );
        }
    }
}
