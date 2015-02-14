package com.enonic.xp.data;


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
            
            this.id = property.getId().toString();
        }
        else
        {
            this.v = property.getValue().toJsonValue();
            this.id = property.getId().toString();
        }
    }

    void fromJson( final PropertyArray array, final ValueType type )
    {
        final PropertyId propertyId = new PropertyId( this.id );
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
                value = Value.newData( newSet );
            }
            else
            {
                value = Value.newData( null );
            }
        }
        else
        {
            value = type.fromJsonValue( v );
        }

        final Property newProperty = new Property( array.getName(), array.size(), value, propertyId, array.getParent() );
        array.addProperty( newProperty );
    }
}
