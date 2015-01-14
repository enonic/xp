package com.enonic.wem.api.data;

import java.util.ArrayList;
import java.util.List;

public class PropertySetJson
{
    public static PropertySet fromJson( final List<PropertyArrayJson> list )
    {
        final PropertySet set = new PropertySet();
        for ( PropertyArrayJson propertyArrayJson : list )
        {
            propertyArrayJson.fromJson( set );
        }
        return set;
    }

    public static List<PropertyArrayJson> toJson( final PropertySet set )
    {
        final List<PropertyArrayJson> list = new ArrayList<>();
        for ( final PropertyArray propertyArray : set.getPropertyArrays() )
        {
            list.add( PropertyArrayJson.toJson( propertyArray ) );
        }
        return list;
    }
}
