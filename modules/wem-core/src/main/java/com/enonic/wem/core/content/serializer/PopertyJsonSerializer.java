package com.enonic.wem.core.content.serializer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.type.ValueType;
import com.enonic.wem.api.data.type.ValueTypes;
import com.enonic.wem.core.support.serializer.AbstractJsonSerializer;
import com.enonic.wem.core.support.serializer.JsonSerializerUtil;

import static com.enonic.wem.core.content.serializer.DataJsonSerializer.DATA_NAME;
import static com.enonic.wem.core.content.serializer.DataJsonSerializer.DATA_PATH;
import static com.enonic.wem.core.content.serializer.DataJsonSerializer.DATA_TYPE;
import static com.enonic.wem.core.content.serializer.DataJsonSerializer.DATA_VALUE;


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

        dataObj.put( DATA_NAME, name );
        dataObj.put( DATA_PATH, path );
        dataObj.put( DATA_TYPE, property.getValueType().getName() );
        dataObj.put( DATA_VALUE, property.getString() );
        return dataObj;
    }

    @Override
    protected Property parse( final JsonNode node )
    {
        return parseProperty( node );
    }

    Property parseProperty( final JsonNode dataNode )
    {
        final String name = JsonSerializerUtil.getStringValue( DATA_NAME, dataNode );

        final ValueType dataType = ValueTypes.parseByName( JsonSerializerUtil.getStringValue( DATA_TYPE, dataNode, null ) );
        Preconditions.checkNotNull( dataType, "dataType was null" );

        final JsonNode valueNode = dataNode.get( DATA_VALUE );

        return dataType.newProperty( name, valueNode.textValue() );
    }
}
