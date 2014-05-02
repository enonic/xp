package com.enonic.wem.api.data.serializer;

import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.data.Data;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.type.ValueType;
import com.enonic.wem.api.data.type.ValueTypes;
import com.enonic.wem.api.support.serializer.AbstractJsonSerializer;
import com.enonic.wem.api.support.serializer.JsonSerializerUtil;

import static com.enonic.wem.api.data.serializer.DataJsonSerializer.DATA_NAME;
import static com.enonic.wem.api.data.serializer.DataJsonSerializer.DATA_TYPE;
import static com.enonic.wem.api.data.serializer.DataJsonSerializer.DATA_VALUE;


public class PropertyJsonSerializer
    extends AbstractJsonSerializer<Property>
{
    private final DataJsonSerializer dataSerializer;

    public PropertyJsonSerializer( final DataJsonSerializer dataSerializer )
    {
        this.dataSerializer = dataSerializer;
    }

    public PropertyJsonSerializer()
    {
        this.dataSerializer = new DataJsonSerializer();
    }

    public PropertyJsonSerializer( final DataJsonSerializer dataSerializer, final ObjectMapper objectMapper )
    {
        super( objectMapper );
        this.dataSerializer = dataSerializer;
    }

    @Override
    protected JsonNode serialize( final Property property )
    {
        final ObjectNode dataObj = objectMapper().createObjectNode();

        final String name = property.getName();

        dataObj.put( DATA_NAME, name );
        dataObj.put( DATA_TYPE, property.getValueType().getName() );
        if ( property.getValueType().equals( ValueTypes.DATA ) )
        {
            final RootDataSet rootDataSet = property.getValue().asData();
            final ArrayNode dataArrayNode = dataObj.arrayNode();
            for ( Data data : rootDataSet )
            {
                dataArrayNode.add( dataSerializer.serialize( data ) );
            }

            dataObj.put( DATA_VALUE, dataArrayNode );
        }
        else
        {
            dataObj.put( DATA_VALUE, property.getString() );
        }
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

        final ValueType valueType = ValueTypes.parseByName( JsonSerializerUtil.getStringValue( DATA_TYPE, dataNode, null ) );
        Preconditions.checkNotNull( valueType, "valueType was null" );

        if ( valueType.equals( ValueTypes.DATA ) )
        {
            final RootDataSet rootDataSet = new RootDataSet();
            final ArrayNode valueArray = (ArrayNode) dataNode.get( DATA_VALUE );
            final Iterator<JsonNode> dataIt = valueArray.elements();
            while ( dataIt.hasNext() )
            {
                final Data data = dataSerializer.parse( dataIt.next() );
                rootDataSet.add( data );
            }
            return Property.newProperty( name, valueType.newValue( rootDataSet ) );
        }
        else
        {
            final JsonNode valueNode = dataNode.get( DATA_VALUE );
            return Property.newProperty( name, valueType.newValue( valueNode.textValue() ) );
        }
    }
}
