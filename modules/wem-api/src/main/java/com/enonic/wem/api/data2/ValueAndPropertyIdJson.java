package com.enonic.wem.api.data2;


import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ValueAndPropertyIdJson
{
    public Object v;

    public List<PropertyArrayJson> set;

    public String id;

    public ValueAndPropertyIdJson()
    {
        // Needed for Jackson
    }

    ValueAndPropertyIdJson( final Property property )
    {
        if ( property.getValueType().equals( ValueTypes.PROPERTY_SET ) )
        {
            final PropertySet propertySet = property.getSet();
            final List<PropertyArrayJson> propertyArrayJsonList = new ArrayList<>();
            for ( final PropertyArray propertyArray : propertySet.getPropertyArrays() )
            {
                propertyArrayJsonList.add( PropertyArrayJson.toJson( propertyArray ) );
            }
            this.set = propertyArrayJsonList;
            this.id = property.getId().toString();
        }
        else
        {
            this.v = property.getValue().toJsonValue();
            this.id = property.getId().toString();
        }
    }

    void fromJson( final PropertySet parent, final ValueType type, final String propertyName )
    {
        final Value value;
        if ( type.equals( ValueTypes.PROPERTY_SET ) )
        {
            final PropertySet newSet = parent.addSet( propertyName );
            final List<PropertyArrayJson> propertyArrayJsonList = (List<PropertyArrayJson>) set;
            for ( final PropertyArrayJson propertyArrayJson : propertyArrayJsonList )
            {
                propertyArrayJson.fromJson( newSet );
            }
        }
        else
        {
            value = type.fromJsonValue( v );
            parent.addProperty( propertyName, value );
        }
    }
}
