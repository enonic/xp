package com.enonic.xp.data;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class PropertyTreeJson
{
    public static PropertyTree fromJson( final List<PropertyArrayJson> list )
    {
        final PropertyTree tree = new PropertyTree( list.size() );
        final PropertySet propertySet = tree.getRoot();
        for ( PropertyArrayJson propertyArrayJson : list )
        {
            fromArrayJson( propertyArrayJson, propertySet );
        }
        return tree;
    }

    public static List<PropertyArrayJson> toJson( final PropertyTree propertyTree )
    {
        final List<PropertyArrayJson> list = new ArrayList<>();
        for ( final PropertyArray propertyArray : propertyTree.getRoot().getPropertyArrays() )
        {
            list.add( propertyArrayToJson( propertyArray ) );
        }
        return list;
    }

    private static void fromArrayJson( PropertyArrayJson from, final PropertySet parent )
    {
        final ValueType valueType = ValueTypes.getByName( from.type );
        final PropertyArray array = new PropertyArray( parent, from.name, valueType, from.values.size() );

        for ( final PropertyValueJson valueJson : from.values )
        {
            fromValueJson( valueJson, array, valueType );
        }

        parent.addPropertyArray( array );
    }

    private static void fromValueJson( PropertyValueJson valueJson, final PropertyArray into, final ValueType type )
    {
        final Value value;
        if ( type.equals( ValueTypes.PROPERTY_SET ) )
        {
            if ( valueJson.set != null )
            {
                final PropertySet newSet = new PropertySet( into.getParent().getTree(), valueJson.set.size() );

                for ( final PropertyArrayJson propertyArrayJson : valueJson.set )
                {
                    fromArrayJson( propertyArrayJson, newSet );
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
            value = type.fromJsonValue( valueJson.v );
        }

        into.addValue( value );
    }

    static PropertyArrayJson propertyArrayToJson( final PropertyArray propertyArray )
    {
        final PropertyArrayJson json = new PropertyArrayJson();
        json.name = propertyArray.getName();
        json.type = ValueTypes.DATE_TIME.equals( propertyArray.getValueType() ) ? "Instant" : propertyArray.getValueType().getName();

        json.values = new ArrayList<>( propertyArray.size() );
        for ( final Property property : propertyArray.getProperties() )
        {
            json.values.add( propertyToJson( property ) );
        }

        return json;
    }

    private static PropertyValueJson propertyToJson( final Property property) {
        final PropertyValueJson json = new PropertyValueJson();
        if ( property.getType().equals( ValueTypes.PROPERTY_SET ) )
        {
            final PropertySet propertySet = property.getSet();
            if ( propertySet != null )
            {
                final List<PropertyArrayJson> propertyArrayJsonList = new ArrayList<>();

                for ( final PropertyArray propertyArray : propertySet.getPropertyArrays() )
                {
                    propertyArrayJsonList.add( propertyArrayToJson( propertyArray ) );
                }
                json.set = propertyArrayJsonList;
            }
        }
        else
        {
            json.v = property.getValue().toJsonValue();
        }
        return json;
    }
}
