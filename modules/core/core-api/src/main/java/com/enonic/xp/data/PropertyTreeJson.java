package com.enonic.xp.data;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class PropertyTreeJson
{
    public static PropertyTree fromJson( final List<PropertyArrayJson> list )
    {
        final PropertyTree tree = new PropertyTree();
        for ( PropertyArrayJson propertyArrayJson : list )
        {
            propertyArrayJson.fromJson( tree.getRoot() );
        }
        return tree;
    }

    public static List<PropertyArrayJson> toJson( final PropertyTree propertyTree )
    {
        final List<PropertyArrayJson> list = new ArrayList<>();
        for ( final PropertyArray propertyArray : propertyTree.getRoot().getPropertyArrays() )
        {
            list.add( PropertyArrayJson.toJson( propertyArray ) );
        }
        return list;
    }
}
