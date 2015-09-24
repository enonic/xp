package com.enonic.wem.repo.internal.elasticsearch.query.translator;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import com.enonic.wem.repo.internal.elasticsearch.query.DiffQueryFactory;
import com.enonic.wem.repo.internal.elasticsearch.query.ElasticsearchQuery;
import com.enonic.wem.repo.internal.search.SearchRequest;
import com.enonic.wem.repo.internal.storage.ReturnFields;
import com.enonic.wem.repo.internal.storage.StaticStorageType;
import com.enonic.wem.repo.internal.version.NodeVersionDiffQuery;
import com.enonic.wem.repo.internal.version.VersionIndexPath;

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
