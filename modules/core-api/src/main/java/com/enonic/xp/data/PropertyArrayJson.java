package com.enonic.xp.data;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PropertyArrayJson
{
    @JsonProperty("name")
    private String name;

    @JsonProperty("type")
    private String type;

    @JsonProperty("values")
    private List<ValueAndPropertyIdJson> values;

    public PropertyArrayJson()
    {
    }

    static PropertyArrayJson toJson( final PropertyArray propertyArray )
    {
        final PropertyArrayJson json = new PropertyArrayJson();
        json.name = propertyArray.getName();
        json.type = propertyArray.getValueType().getName();

        json.values = new ArrayList<>( propertyArray.size() );
        for ( final Property property : propertyArray.getProperties() )
        {
            json.values.add( new ValueAndPropertyIdJson( property ) );
        }

        return json;
    }

    void fromJson( final PropertySet parent )
    {
        final ValueType valueType = ValueTypes.getByName( type );
        final PropertyArray array = new PropertyArray( parent.getTree(), parent, name, valueType );

        for ( final ValueAndPropertyIdJson valueJson : values )
        {
            valueJson.fromJson( array, valueType );
        }

        parent.addPropertyArray( array );
    }
}
