package com.enonic.xp.repo.impl.elasticsearch.query.translator;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import com.enonic.xp.repo.impl.ReturnFields;
import com.enonic.xp.repo.impl.elasticsearch.query.DiffQueryFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.ElasticsearchQuery;
import com.enonic.xp.repo.impl.search.SearchRequest;
import com.enonic.xp.repo.impl.storage.StaticStorageType;
import com.enonic.xp.repo.impl.version.VersionIndexPath;
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
            index( request.getSettings().getStorageName().getName() ).
            indexType( request.getSettings().getStorageType().getName() ).
            query( queryBuilder ).
            setReturnFields( ReturnFields.from( VersionIndexPath.NODE_ID, VersionIndexPath.VERSION_ID, VersionIndexPath.TIMESTAMP ) ).
            size( query.getSize() ).
            from( query.getFrom() ).
            addSortBuilder( new FieldSortBuilder( VersionIndexPath.NODE_PATH.getPath() ).order( SortOrder.ASC ) ).
            build();
    }
}
