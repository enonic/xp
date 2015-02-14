package com.enonic.xp.core.impl.export.xml.mapper;


import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.export.ExportNodeException;
import com.enonic.xp.core.impl.export.xml.ObjectFactory;
import com.enonic.xp.core.impl.export.xml.XmlPropertyTree;

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
            if ( property.getType().equals( ValueTypes.PROPERTY_SET ) )
            {
                xml.getList().add( XmlPropertySetMapper.toXml( property, objectFactory ) );
            }
            else
            {
                xml.getList().add( XmlPropertyMapper.toXml( property, objectFactory ) );
            }
        }

        return xml;
    }
}
