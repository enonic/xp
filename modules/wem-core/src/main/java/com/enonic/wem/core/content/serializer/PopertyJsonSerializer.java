package com.enonic.wem.core.content.serializer;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.data.Property;
import com.enonic.wem.api.content.data.type.BaseValueType;
import com.enonic.wem.api.content.data.type.ValueTypes;
import com.enonic.wem.core.support.serializer.AbstractJsonSerializer;
import com.enonic.wem.core.support.serializer.JsonSerializerUtil;

import static com.enonic.wem.core.content.serializer.EntryJsonSerializer.ENTRY_NAME;
import static com.enonic.wem.core.content.serializer.EntryJsonSerializer.ENTRY_PATH;
import static com.enonic.wem.core.content.serializer.EntryJsonSerializer.ENTRY_TYPE;
import static com.enonic.wem.core.content.serializer.EntryJsonSerializer.ENTRY_VALUE;


public class PopertyJsonSerializer
    extends AbstractJsonSerializer<Property>
{
    public PopertyJsonSerializer()
    {
        // default
    }

    public PopertyJsonSerializer( final ObjectMapper objectMapper )
    {
        super( objectMapper );
    }

    @Override
    protected JsonNode serialize( final Property property )
    {
        final ObjectNode dataObj = objectMapper().createObjectNode();

        final String name = property.getName();
        final String path = property.getPath().toString();

        dataObj.put( ENTRY_NAME, name );
        dataObj.put( ENTRY_PATH, path );
        dataObj.put( ENTRY_TYPE, property.getType().getName() );
        dataObj.put( ENTRY_VALUE, property.getString() );
        return dataObj;
    }

    @Override
    protected Property parse( final JsonNode node )
    {
        return parseProperty( node );
    }

    Property parseProperty( final JsonNode dataNode )
    {
        final String name = JsonSerializerUtil.getStringValue( ENTRY_NAME, dataNode );

        final BaseValueType dataType =
            (BaseValueType) ValueTypes.parseByName( JsonSerializerUtil.getStringValue( ENTRY_TYPE, dataNode, null ) );
        Preconditions.checkNotNull( dataType, "dataType was null" );

        final JsonNode valueNode = dataNode.get( ENTRY_VALUE );

        return dataType.newProperty( name, valueNode.getTextValue() );
    }
}
