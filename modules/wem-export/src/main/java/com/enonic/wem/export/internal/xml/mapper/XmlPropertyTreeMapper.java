package com.enonic.wem.export.internal.xml.mapper;


import com.enonic.wem.api.data2.Property;
import com.enonic.wem.api.data2.PropertyTree;
import com.enonic.wem.export.ExportNodeException;
import com.enonic.wem.export.internal.xml.ObjectFactory;
import com.enonic.wem.export.internal.xml.XmlPropertyTree;

class XmlPropertyTreeMapper
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
        final ObjectFactory objectFactory = new ObjectFactory();

        final XmlPropertyTree xml = new XmlPropertyTree();

        for ( final Property property : propertyTree.getProperties() )
        {
            xml.getList().add( XmlPropertyMapper.toXml( property, objectFactory ) );
        }

        // xml.setProperties( xmlProperties );

        return xml;
    }


}
