package com.enonic.wem.core.entity.dao;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;

import com.google.inject.Inject;

import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityIds;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.core.index.Index;
import com.enonic.wem.core.index.IndexType;
import com.enonic.wem.core.index.elastic.ByIdQuery;
import com.enonic.wem.core.index.elastic.ByIdsQuery;
import com.enonic.wem.core.index.elastic.ByParentPathQuery;
import com.enonic.wem.core.index.elastic.ElasticsearchIndexService;
import com.enonic.wem.core.index.elastic.IndexDocumentId;

public class NodeElasticsearchDao
{
    @Inject
    private ElasticsearchIndexService elasticsearchIndexService;

    private final static Index index = Index.NODB;

    private final static IndexType indexType = IndexType.NODE;

    public Nodes getByIds( final EntityIds entityIds )
    {
        if ( entityIds.isEmpty() )
        {
            return Nodes.empty();
        }

        final ByIdsQuery.Builder builder = ByIdsQuery.
            byIds().
            index( index ).
            indexType( indexType );

        for ( final EntityId entityId : entityIds )
        {
            builder.add( new IndexDocumentId( entityId.toString() ) );
        }

        final SearchResponse searchResponse = elasticsearchIndexService.get( builder.build() );

        return ElasticsearchResponseNodeTranslator.toNodes( searchResponse );
    }

    public Node getById( final EntityId entityId )
    {
        final GetResponse getResponse = elasticsearchIndexService.get( ByIdQuery.byId( entityId.toString() ).
            index( index ).
            indexType( indexType ).
            build() );

        if ( !getResponse.isExists() )
        {
            throw new NodeNotFoundException( "Node with id " + entityId + " not found" );
        }

        return ElasticsearchResponseNodeTranslator.toNode( getResponse );
    }

    public Nodes getByParent( final NodePath parent )
    {

        final SearchResponse searchResponse = elasticsearchIndexService.get( ByParentPathQuery.byParentPath( parent.toString() ).
            index( index ).
            indexType( indexType ).
            build() );

        return ElasticsearchResponseNodeTranslator.toNodes( searchResponse );
    }

}
