package com.enonic.wem.core.data.serializer;


import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.data.Data;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.core.support.serializer.AbstractJsonSerializer;

public class RootDataSetJsonSerializer
    extends AbstractJsonSerializer<RootDataSet>
{
    private DataJsonSerializer dataSerializer;


    public RootDataSetJsonSerializer()
    {
        super();
        dataSerializer = new DataJsonSerializer( objectMapper() );
    }

    public RootDataSetJsonSerializer( final ObjectMapper objectMapper )
    {
        super( objectMapper );
        dataSerializer = new DataJsonSerializer( objectMapper );
    }

    @Override
    public JsonNode serialize( final RootDataSet rootDataSet )
    {
        final ArrayNode arrayNode = objectMapper().createArrayNode();
        for ( Data data : rootDataSet )
        {
            arrayNode.add( dataSerializer.serialize( data ) );
        }
        return arrayNode;
    }

    @Override
    public RootDataSet parse( final JsonNode node )
    {
        Preconditions.checkNotNull( node, "node cannot be null" );
        Preconditions.checkArgument( node instanceof ArrayNode, "node expected to be a ArrayNode" );

        final RootDataSet rootDataSet = new RootDataSet();
        @SuppressWarnings("ConstantConditions") final ArrayNode arrayNode = (ArrayNode) node;
        parseEntries( arrayNode, rootDataSet );
        return rootDataSet;
    }

    private void parseEntries( final ArrayNode arrayNode, final DataSet dataSet )
    {
        final Iterator<JsonNode> dataIt = arrayNode.elements();
        while ( dataIt.hasNext() )
        {
            final JsonNode dataNode = dataIt.next();
            final Data data = dataSerializer.parse( dataNode );
            dataSet.add( data );
        }
    }
}
