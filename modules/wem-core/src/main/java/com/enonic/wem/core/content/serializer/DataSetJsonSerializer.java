package com.enonic.wem.core.content.serializer;


import java.util.Iterator;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.core.support.serializer.AbstractJsonSerializer;
import com.enonic.wem.core.support.serializer.JsonSerializerUtil;

import static com.enonic.wem.core.content.serializer.DataJsonSerializer.DATA_NAME;
import static com.enonic.wem.core.content.serializer.DataJsonSerializer.DATA_PATH;
import static com.enonic.wem.core.content.serializer.DataJsonSerializer.DATA_TYPE;
import static com.enonic.wem.core.content.serializer.DataJsonSerializer.DATA_VALUE;

public class DataSetJsonSerializer
    extends AbstractJsonSerializer<DataSet>
{
    private DataJsonSerializer dataSerializer;

    public DataSetJsonSerializer( final ObjectMapper objectMapper )
    {
        super( objectMapper );
        dataSerializer = new DataJsonSerializer( objectMapper, this );
    }

    public DataSetJsonSerializer()
    {
        dataSerializer = new DataJsonSerializer( objectMapper(), this );
    }

    @Override
    protected JsonNode serialize( final DataSet dataSet )
    {
        final ObjectNode dataSetObj = objectMapper().createObjectNode();

        final String name = dataSet.getName();
        final String path = dataSet.getPath().toString();

        dataSetObj.put( DATA_NAME, name );
        dataSetObj.put( DATA_PATH, path );
        dataSetObj.put( DATA_TYPE, DataSet.class.getSimpleName() );
        dataSetObj.put( DATA_VALUE, serializeEntries( dataSet ) );
        return dataSetObj;
    }

    JsonNode serializeEntries( final DataSet dataSet )
    {
        final ArrayNode arrayNode = objectMapper().createArrayNode();
        for ( Data data : dataSet )
        {
            arrayNode.add( dataSerializer.serialize( data ) );
        }
        return arrayNode;
    }

    @Override
    protected DataSet parse( final JsonNode dataSetNode )
    {
        Preconditions.checkNotNull( dataSetNode, "dataSetNode cannot be null" );

        final DataSet contentData = new ContentData();
        final ArrayNode arrayNode = (ArrayNode) dataSetNode.get( DATA_VALUE );

        parseEntries( arrayNode, contentData );
        return contentData;
    }

    DataSet parseDataSet( final JsonNode dataSetNode )
    {
        final String name = JsonSerializerUtil.getStringValue( DATA_NAME, dataSetNode );
        final DataSet dataSet = DataSet.newDataSet().name( name ).build();
        final ArrayNode entriesArray = (ArrayNode) dataSetNode.get( DATA_VALUE );
        parseEntries( entriesArray, dataSet );
        return dataSet;
    }

    void parseEntries( final ArrayNode arrayNode, final DataSet dataSet )
    {
        final Iterator<JsonNode> dataIt = arrayNode.getElements();
        while ( dataIt.hasNext() )
        {
            final JsonNode dataNode = dataIt.next();
            final Data data = dataSerializer.parse( dataNode );
            dataSet.add( data );
        }
    }
}
