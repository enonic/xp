package com.enonic.wem.api.data.serializer;


import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.data.Data;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.support.serializer.AbstractJsonSerializer;
import com.enonic.wem.api.support.serializer.JsonSerializerUtil;

import static com.enonic.wem.api.data.serializer.DataJsonSerializer.DATA_NAME;
import static com.enonic.wem.api.data.serializer.DataJsonSerializer.DATA_SET;

public class DataSetJsonSerializer
    extends AbstractJsonSerializer<DataSet>
{
    private DataJsonSerializer dataSerializer;

    public DataSetJsonSerializer( final ObjectMapper objectMapper )
    {
        super( objectMapper );
        dataSerializer = new DataJsonSerializer( objectMapper, this );
    }

    @Override
    protected JsonNode serialize( final DataSet dataSet )
    {
        final ObjectNode dataSetObj = objectMapper().createObjectNode();

        final String name = dataSet.getName();

        dataSetObj.put( DATA_NAME, name );
        dataSetObj.set( DATA_SET, serializeEntries( dataSet ) );
        return dataSetObj;
    }

    public JsonNode serializeEntries( final DataSet dataSet )
    {
        final ArrayNode arrayNode = objectMapper().createArrayNode();
        for ( Data data : dataSet )
        {
            arrayNode.add( dataSerializer.serialize( data ) );
        }
        return arrayNode;
    }

    @Override
    protected DataSet parse( final JsonNode node )
    {
        Preconditions.checkNotNull( node, "dataSetNode cannot be null" );
        Preconditions.checkArgument( node instanceof ObjectNode, "node expected to be a ObjectNode" );
        @SuppressWarnings("ConstantConditions") ObjectNode dataSetNode = (ObjectNode) node;

        return parseDataSet( dataSetNode );
    }

    DataSet parseDataSet( final ObjectNode dataSetNode )
    {
        final String name = JsonSerializerUtil.getStringValue( DATA_NAME, dataSetNode );
        final DataSet dataSet = DataSet.create().name( name ).build();
        final ArrayNode entriesArray = (ArrayNode) dataSetNode.get( DATA_SET );
        parseEntries( entriesArray, dataSet );
        return dataSet;
    }

    public void parseEntries( final ArrayNode arrayNode, final DataSet dataSet )
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
