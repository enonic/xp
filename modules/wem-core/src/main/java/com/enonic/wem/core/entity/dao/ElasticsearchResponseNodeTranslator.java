package com.enonic.wem.core.entity.dao;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.get.GetField;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.elasticsearch.search.SearchHits;

import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.core.entity.json.NodeJsonSerializer;

public class ElasticsearchResponseNodeTranslator
{
    public static Nodes toNodes( final SearchResponse searchResponse )
    {
        if ( searchResponse.getHits().getHits().length == 0 )
        {
            return Nodes.empty();
        }

        final Nodes.Builder nodesBuilder = Nodes.newNodes();

        final SearchHits hits = searchResponse.getHits();

        for ( final SearchHit hit : hits )
        {
            final SearchHitField serializedData = getSerializedData( hit );

            final Node node = NodeJsonSerializer.toNode( serializedData.getValue().toString() );

            nodesBuilder.add( node );
        }

        return nodesBuilder.build();
    }

    public static Node toNode( final GetResponse getResponse )
    {
        final GetField field = getResponse.getField( NodeStorageDocumentFactory.ENTITY );

        if ( field == null )
        {
            throw new IllegalArgumentException( "Expected serialized data of node in index" );
        }

        return NodeJsonSerializer.toNode( field.getValue().toString() );
    }


    private static SearchHitField getSerializedData( final SearchHit hit )
    {
        final SearchHitField serializedData = hit.field( NodeStorageDocumentFactory.ENTITY );

        if ( serializedData == null )
        {
            throw new IllegalArgumentException( "Expected serialized data of node in index for id " + hit.getId() );
        }
        return serializedData;
    }


}
