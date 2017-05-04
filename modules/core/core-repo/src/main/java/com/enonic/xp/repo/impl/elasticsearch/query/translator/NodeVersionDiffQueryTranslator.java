package com.enonic.xp.repo.impl.elasticsearch.query.translator;

import org.elasticsearch.index.query.QueryBuilder;

import com.enonic.xp.repo.impl.elasticsearch.query.DiffQueryFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.ElasticsearchQuery;
import com.enonic.xp.repo.impl.search.SearchRequest;
import com.enonic.xp.repo.impl.storage.StaticStorageType;
import com.enonic.xp.repo.impl.version.search.NodeVersionDiffQuery;

public class NodeVersionDiffQueryTranslator
{
    public ElasticsearchQuery translate( final SearchRequest request )
    {
        final NodeVersionDiffQuery query = (NodeVersionDiffQuery) request.getQuery();

        final QueryBuilder queryBuilder = DiffQueryFactory.create().
            query( query ).
            childStorageType( StaticStorageType.BRANCH ).
            build().
            execute();

        return ElasticsearchQuery.create().
            addIndexName( request.getSettings().getStorageName().getName() ).
            addIndexType( request.getSettings().getStorageType().getName() ).
            query( queryBuilder ).
            setReturnFields( request.getReturnFields() ).
            size( query.getSize() ).
            batchSize( query.getBatchSize() ).
            from( query.getFrom() ).
            build();
    }
}
