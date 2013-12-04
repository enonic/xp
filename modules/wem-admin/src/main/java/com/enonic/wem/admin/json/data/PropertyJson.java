package com.enonic.wem.admin.json.data;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.RootDataSet;
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
                         @JsonProperty(value = "value") final String value, @JsonProperty(value = "set") final List<DataJson> set )
    {
        super( new Property( name, ValueTypes.parseByName( type ).newValue(
            value != null ? value : RootDataSetJson.dataJsonListToRootDataSet( set ) ) ) );
        this.property = getData();
    }

    public String getType()
    {
        return property.getValueType().getName();
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getValue()
    {
        if ( !property.getValue().getType().equals( ValueTypes.DATA ) )
        {
            return property.getString();
        }
        else
        {
            return null;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public List<DataJson> getSet()
    {
        if ( property.getValue().getType().equals( ValueTypes.DATA ) )
        {
            final RootDataSet rootDataSet = property.getValue().getData();
            final RootDataSetJson rootDataSetJson = new RootDataSetJson( rootDataSet );
            return rootDataSetJson.getSet();
        }
        else
        {
            return null;
        }
    }
}
