package com.enonic.xp.core.impl.export.xml.mapper;


import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.PropertySet;
import com.enonic.wem.api.data.ValueTypes;
import com.enonic.wem.api.export.ExportNodeException;
import com.enonic.xp.core.impl.export.xml.ObjectFactory;
import com.enonic.xp.core.impl.export.xml.XmlPropertySet;

class XmlPropertySetMapper
{

    public static Object toXml( final Property property, final ObjectFactory objectFactory )
    {
        try
        {
            final XmlPropertySet xmlPropertySet = doSerializePropertySet( property.getSet(), objectFactory );
            xmlPropertySet.setName( property.getName() );

            return xmlPropertySet;
        }
        catch ( Exception e )
        {
            throw new ExportNodeException( "Failed to serialize node to xml", e );
        }
    }

    private static XmlPropertySet doSerializePropertySet( final PropertySet propertySet, final ObjectFactory objectFactory )
    {
        final XmlPropertySet xml = new XmlPropertySet();

        if ( propertySet != null )
        {
            for ( final Property property : propertySet.getProperties() )
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
        }
        else
        {
            xml.setIsNull( true );
        }

        return xml;
    }


}
