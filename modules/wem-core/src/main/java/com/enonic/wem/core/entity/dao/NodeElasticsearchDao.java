package com.enonic.wem.core.entity.dao;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;

import com.google.inject.Inject;

import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityIds;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.NodePaths;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.core.index.Index;
import com.enonic.wem.core.index.IndexType;
import com.enonic.wem.core.index.elastic.ByIdQuery;
import com.enonic.wem.core.index.elastic.ByIdsQuery;
import com.enonic.wem.core.index.elastic.ByParentPathQuery;
import com.enonic.wem.core.index.elastic.ByPathQuery;
import com.enonic.wem.core.index.elastic.ByPathsQuery;
import com.enonic.wem.core.index.elastic.ElasticsearchIndexService;
import com.enonic.wem.core.index.elastic.IndexDocumentId;

public class NodeElasticsearchDao
{
    @Inject
    private ElasticsearchIndexService elasticsearchIndexService;

    private final static Index index = Index.NODB;

    private final static IndexType indexType = IndexType.NODE;

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

        verifyGetResult( searchResponse, entityIds.getSize(), entityIds.getSize() );

        return ElasticsearchResponseNodeTranslator.toNodes( searchResponse );
    }

    public Node getByPath( final NodePath path )
    {
        final SearchResponse searchResponse = elasticsearchIndexService.get( ByPathQuery.byPath( path.toString() ).
            index( index ).
            indexType( indexType ).
            build() );

        verifyGetResult( searchResponse, 1, 1 );

        final Nodes nodes = ElasticsearchResponseNodeTranslator.toNodes( searchResponse );

        return nodes.get( 0 );
    }

    public Nodes getByPaths( final NodePaths paths )
    {
        final SearchResponse searchResponse = elasticsearchIndexService.get( ByPathsQuery.byPaths().
            setPaths( paths ).
            index( index ).
            indexType( indexType ).
            build() );

        verifyGetResult( searchResponse, paths.getSize(), paths.getSize() );

        return ElasticsearchResponseNodeTranslator.toNodes( searchResponse );
    }


    public Nodes getByParent( final NodePath parent )
    {
        final SearchResponse searchResponse = elasticsearchIndexService.get( ByParentPathQuery.byParentPath( parent.toString() ).
            index( index ).
            indexType( indexType ).
            build() );

        verifyGetResult( searchResponse, null, null );

        return ElasticsearchResponseNodeTranslator.toNodes( searchResponse );
    }

    public void verifyGetResult( final SearchResponse searchResponse, final Integer min, final Integer max )
    {
        final int length = searchResponse.getHits().getHits().length;

        if ( min != null && length < min )
        {
            throw new IllegalArgumentException( "Expected at least: " + min + " results, actual " + length );
        }

        if ( max != null && length > max )
        {
            throw new IllegalArgumentException( "Expected at most: " + max + " results, actual " + length );
        }

    }

}
