package com.enonic.wem.admin.json.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.type.ValueTypes;

public class PropertyJson
    extends DataJson<Property>
{
    private final Property property;

    public PropertyJson( final Property property )
    {
        super( property );
        this.property = property;
    }

    @JsonCreator
    public PropertyJson( @JsonProperty("name") final String name, @JsonProperty("type") final String type,
                         @JsonProperty("value") final String valueAsString )
    {
        super( new Property( name, ValueTypes.parseByName( type ).newValue( valueAsString ) ) );
        this.property = getData();
    }

    public String getType()
    {
        return property.getValueType().getName();
    }

    public String getValue()
    {
        return property.getString();
    }
}
