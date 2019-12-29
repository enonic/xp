package com.enonic.xp.data;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class PropertyValueJson
{
    public Object v;

    public List<PropertyArrayJson> set;

    public PropertyValueJson()
    {
        // Needed for Jackson
    }

    PropertyValueJson( final Property property )
    {
        if ( property.getType().equals( ValueTypes.PROPERTY_SET ) )
        {
            final PropertySet propertySet = property.getSet();
            if ( propertySet != null )
            {
                final List<PropertyArrayJson> propertyArrayJsonList = new ArrayList<>();

                for ( final PropertyArray propertyArray : propertySet.getPropertyArrays() )
                {
                    propertyArrayJsonList.add( PropertyArrayJson.toJson( propertyArray ) );
                }
                this.set = propertyArrayJsonList;
            }
        }
        else
        {
            this.v = property.getValue().toJsonValue();
        }
    }

    void fromJson( final PropertyArray array, final ValueType type )
    {
        final Value value;
        if ( type.equals( ValueTypes.PROPERTY_SET ) )
        {
            if ( this.set != null )
            {
                final PropertySet newSet = array.newSet();
                for ( final PropertyArrayJson propertyArrayJson : set )
                {
                    propertyArrayJson.fromJson( newSet );
                }
                value = ValueFactory.newPropertySet( newSet );
            }
            else
            {
                value = ValueFactory.newPropertySet( null );
            }
        }
        else
        {
            value = type.fromJsonValue( v );
        }

        final Property newProperty = new Property( array.getName(), array.size(), value, array.getParent() );
        array.addProperty( newProperty );
    }
}
