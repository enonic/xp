package com.enonic.xp.data;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class PropertyArrayJson
{
    public String name;

    public String type;

    public List<PropertyValueJson> values;

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
            json.values.add( new PropertyValueJson( property ) );
        }

        return json;
    }

    void fromJson( final PropertySet parent )
    {
        final ValueType valueType = ValueTypes.getByName( type );
        final PropertyArray array = new PropertyArray( parent.getTree(), parent, name, valueType );

        for ( final PropertyValueJson valueJson : values )
        {
            valueJson.fromJson( array, valueType );
        }

        parent.addPropertyArray( array );
    }
}
