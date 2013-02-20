package com.enonic.wem.core.content.serializer;


import java.util.Iterator;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.data.Entry;
import com.enonic.wem.api.content.data.type.DataTypes;
import com.enonic.wem.core.support.serializer.AbstractJsonSerializer;
import com.enonic.wem.core.support.serializer.JsonSerializerUtil;

import static com.enonic.wem.core.content.serializer.EntryJsonSerializer.ENTRY_NAME;
import static com.enonic.wem.core.content.serializer.EntryJsonSerializer.ENTRY_PATH;
import static com.enonic.wem.core.content.serializer.EntryJsonSerializer.ENTRY_TYPE;
import static com.enonic.wem.core.content.serializer.EntryJsonSerializer.ENTRY_VALUE;

public class DataSetJsonSerializer
    extends AbstractJsonSerializer<DataSet>
{
    private EntryJsonSerializer entrySerializer;

    public DataSetJsonSerializer( final ObjectMapper objectMapper )
    {
        super( objectMapper );
        entrySerializer = new EntryJsonSerializer( objectMapper, this );
    }

    public DataSetJsonSerializer()
    {
        entrySerializer = new EntryJsonSerializer( objectMapper(), this );
    }

    @Override
    protected JsonNode serialize( final DataSet dataSet )
    {
        final ObjectNode dataSetObj = objectMapper().createObjectNode();

        final String name = dataSet.getName();
        final String path = dataSet.getPath().toString();

        dataSetObj.put( ENTRY_NAME, name );
        dataSetObj.put( ENTRY_PATH, path );
        dataSetObj.put( ENTRY_TYPE, DataTypes.SET.getName() );
        dataSetObj.put( ENTRY_VALUE, serializeEntries( dataSet ) );
        return dataSetObj;
    }

    JsonNode serializeEntries( final DataSet dataSet )
    {
        final ArrayNode arrayNode = objectMapper().createArrayNode();
        for ( Entry entry : dataSet )
        {
            arrayNode.add( entrySerializer.serialize( entry ) );
        }
        return arrayNode;
    }

    @Override
    protected DataSet parse( final JsonNode dataSetNode )
    {
        Preconditions.checkNotNull( dataSetNode, "dataSetNode cannot be null" );

        final DataSet rootDataSet = DataSet.newRootDataSet();
        final ArrayNode arrayNode = (ArrayNode) dataSetNode.get( ENTRY_VALUE );

        parseEntries( arrayNode, rootDataSet );
        return rootDataSet;
    }

    DataSet parseDataSet( final JsonNode dataSetNode )
    {
        final String name = JsonSerializerUtil.getStringValue( ENTRY_NAME, dataSetNode );
        final DataSet dataSet = DataSet.newDataSet().name( name ).build();
        final ArrayNode entriesArray = (ArrayNode) dataSetNode.get( ENTRY_VALUE );
        parseEntries( entriesArray, dataSet );
        return dataSet;
    }

    void parseEntries( final ArrayNode arrayNode, final DataSet dataSet )
    {
        final Iterator<JsonNode> dataIt = arrayNode.getElements();
        while ( dataIt.hasNext() )
        {
            final JsonNode dataNode = dataIt.next();
            final Entry entry = entrySerializer.parse( dataNode );
            dataSet.add( entry );
        }
    }
}
