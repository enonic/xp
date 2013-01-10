package com.enonic.wem.core.content.data;

import java.util.Iterator;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.data.Entry;
import com.enonic.wem.api.content.datatype.BaseDataType;
import com.enonic.wem.api.content.datatype.DataTypes;
import com.enonic.wem.core.content.AbstractJsonSerializer;
import com.enonic.wem.core.content.JsonParserUtil;

final class EntryJsonSerializer
    extends AbstractJsonSerializer<Entry>
{
    private static final String DATA_VALUE = "value";

    private static final String DATA_NAME = "name";

    private static final String DATA_TYPE = "type";

    private static final String DATA_PATH = "path";

    EntryJsonSerializer( final ObjectMapper objectMapper )
    {
        super( objectMapper );
    }

    protected final JsonNode serialize( final Entry entry )
    {
        return serializeEntry( entry );
    }

    final JsonNode serialize( final ContentData contentData )
    {
        final ArrayNode arrayNode = objectMapper().createArrayNode();
        for ( Entry entry : contentData.getDataSet() )
        {
            arrayNode.add( serializeEntry( entry ) );
        }
        return arrayNode;
    }

    final JsonNode serializeDataSet( final DataSet dataSet )
    {
        final ObjectNode dataObj = objectMapper().createObjectNode();

        final String name = dataSet.getName();
        final String path = dataSet.getPath().toString();

        dataObj.put( DATA_NAME, name );
        dataObj.put( DATA_PATH, path );
        dataObj.put( DATA_TYPE, DataTypes.SET.getName() );

        final ArrayNode arrayNode = objectMapper().createArrayNode();
        for ( Entry entry : dataSet )
        {
            arrayNode.add( serializeEntry( entry ) );
        }
        dataObj.put( DATA_VALUE, arrayNode );
        return dataObj;
    }

    final JsonNode serializeEntry( final Entry entry )
    {
        if ( entry instanceof Data )
        {
            final Data data = (Data) entry;
            return serializeData( data );
        }
        else if ( entry instanceof DataSet )
        {
            final DataSet dataSet = (DataSet) entry;
            return serializeDataSet( dataSet );
        }
        else
        {
            throw new IllegalArgumentException( "Unknown entry: " + entry.getClass().getSimpleName() );
        }
    }

    private JsonNode serializeData( final Data data )
    {
        final ObjectNode dataObj = objectMapper().createObjectNode();

        final String name = data.getName();
        final String path = data.getPath().toString();

        dataObj.put( DATA_NAME, name );
        dataObj.put( DATA_PATH, path );
        dataObj.put( DATA_TYPE, data.getType().getName() );
        dataObj.put( DATA_VALUE, data.asString() );
        return dataObj;
    }

    protected final Entry parse( final JsonNode dataNode )
    {
        return parse( dataNode, null );
    }

    protected final Entry parse( final JsonNode dataNode, final DataSet parent )
    {
        final String name = JsonParserUtil.getStringValue( DATA_NAME, dataNode );

        final BaseDataType dataType = (BaseDataType) DataTypes.parseByName( JsonParserUtil.getStringValue( DATA_TYPE, dataNode, null ) );
        Preconditions.checkNotNull( dataType, "dataType was null" );

        final JsonNode valueNode = dataNode.get( DATA_VALUE );
        if ( valueNode.isArray() )
        {
            final ArrayNode valueArrayNode = (ArrayNode) valueNode;
            final DataSet dataSet = DataSet.newDataSet().name( name ).parent( parent ).build();
            parseDataSet( valueArrayNode, dataSet );
            return dataSet;
        }
        else
        {
            final Data.Builder dataBuilder = Data.newData();
            dataBuilder.parent( parent );
            dataBuilder.name( name );
            dataBuilder.type( dataType );
            dataBuilder.value( valueNode.getTextValue() );
            return dataBuilder.build();
        }
    }

    private void parseDataSet( final ArrayNode arrayNode, final DataSet dataSet )
    {
        final Iterator<JsonNode> dataIt = arrayNode.getElements();
        while ( dataIt.hasNext() )
        {
            final JsonNode dataNode = dataIt.next();
            final Entry entry = parse( dataNode, dataSet );
            dataSet.add( entry );
        }
    }
}
