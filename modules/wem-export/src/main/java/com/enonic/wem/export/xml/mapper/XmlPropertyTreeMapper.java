package com.enonic.wem.export.xml.mapper;

import java.util.ArrayList;
import java.util.List;

import com.enonic.wem.api.data2.Property;
import com.enonic.wem.api.data2.PropertyArrayJson;
import com.enonic.wem.api.data2.PropertyTree;
import com.enonic.wem.api.xml.model.XmlPropertyTree;
import com.enonic.wem.export.ExportNodeException;

public class XmlPropertyTreeMapper
{

    public static XmlPropertyTree toXml( final PropertyTree propertyTree )
    {
        try
        {
            return doSerializePropertyTree( propertyTree );
        }
        catch ( Exception e )
        {
            throw new ExportNodeException( "Failed to serialize node to xml", e );
        }
    }

    private static XmlPropertyTree doSerializePropertyTree( final PropertyTree propertyTree )
    {
        final XmlPropertyTree xml = new XmlPropertyTree();

        final List<PropertyArrayJson> list = new ArrayList<>();
        for ( final Property property : propertyTree.getProperties() )
        {
            //list.add( PropertyArrayJson.toJson( propertyArray ) );
        }

        return xml;

    }


}
