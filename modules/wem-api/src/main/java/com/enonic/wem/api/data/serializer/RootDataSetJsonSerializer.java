package com.enonic.wem.api.data.serializer;


import java.io.IOException;
import java.util.Iterator;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.data.Data;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.support.serializer.AbstractJsonSerializer;
import com.enonic.wem.api.support.serializer.JsonParsingException;
import com.enonic.wem.api.support.serializer.JsonSerializingException;

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

    public String serializeToString( final RootDataSet rootDataSet )
    {
        try
        {
            return objectMapper().writeValueAsString( serialize( rootDataSet ) );
        }
        catch ( JsonProcessingException e )
        {
            throw new JsonSerializingException( "Failed to serialize RootDataSet to String", e );
        }
    }

    @Override
    public JsonNode serialize( final RootDataSet rootDataSet )
    {
        Preconditions.checkNotNull( rootDataSet, "rootDataSet cannot be null" );
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

        //noinspection ConstantConditions
        return parse( (ArrayNode) node );
    }

    public RootDataSet parse( final ArrayNode arrayNode )
    {
        Preconditions.checkNotNull( arrayNode, "arrayNode cannot be null" );
        final RootDataSet rootDataSet = new RootDataSet();
        //noinspection ConstantConditions
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

    public RootDataSet parse( final String jsonString )
    {
        try
        {
            final JsonFactory factory = objectMapper().getFactory();
            final JsonParser parser = factory.createParser( jsonString );
            return parse( (JsonNode) parser.readValueAsTree() );
        }
        catch ( IOException e )
        {
            throw new JsonParsingException( "Failed to parse jsonString", e );
        }
    }

}
