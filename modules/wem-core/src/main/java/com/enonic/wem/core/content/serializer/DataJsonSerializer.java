package com.enonic.wem.core.content.serializer;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.type.BaseDataType;
import com.enonic.wem.api.content.data.type.DataTypes;
import com.enonic.wem.core.support.serializer.AbstractJsonSerializer;
import com.enonic.wem.core.support.serializer.JsonSerializerUtil;

import static com.enonic.wem.api.content.data.Data.newData;
import static com.enonic.wem.core.content.serializer.EntryJsonSerializer.ENTRY_NAME;
import static com.enonic.wem.core.content.serializer.EntryJsonSerializer.ENTRY_PATH;
import static com.enonic.wem.core.content.serializer.EntryJsonSerializer.ENTRY_TYPE;
import static com.enonic.wem.core.content.serializer.EntryJsonSerializer.ENTRY_VALUE;


public class DataJsonSerializer
    extends AbstractJsonSerializer<Data>
{
    public DataJsonSerializer()
    {
        // default
    }

    public DataJsonSerializer( final ObjectMapper objectMapper )
    {
        super( objectMapper );
    }

    @Override
    protected JsonNode serialize( final Data data )
    {
        final ObjectNode dataObj = objectMapper().createObjectNode();

        final String name = data.getName();
        final String path = data.getPath().toString();

        dataObj.put( ENTRY_NAME, name );
        dataObj.put( ENTRY_PATH, path );
        dataObj.put( ENTRY_TYPE, data.getType().getName() );
        dataObj.put( ENTRY_VALUE, data.getString() );
        return dataObj;
    }

    @Override
    protected Data parse( final JsonNode node )
    {
        return parseData( node );
    }

    Data parseData( final JsonNode dataNode )
    {
        final String name = JsonSerializerUtil.getStringValue( ENTRY_NAME, dataNode );

        final BaseDataType dataType =
            (BaseDataType) DataTypes.parseByName( JsonSerializerUtil.getStringValue( ENTRY_TYPE, dataNode, null ) );
        Preconditions.checkNotNull( dataType, "dataType was null" );

        final JsonNode valueNode = dataNode.get( ENTRY_VALUE );

        final Data.Builder dataBuilder = newData();
        dataBuilder.name( name );
        dataBuilder.type( dataType );
        dataBuilder.value( valueNode.getTextValue() );
        Data data = dataBuilder.build();
        return data;
    }
}
