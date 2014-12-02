package com.enonic.wem.export.internal.xml.mapper;

import javax.xml.bind.JAXBElement;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.export.internal.xml.ObjectFactory;
import com.enonic.wem.export.internal.xml.XmlDoubleProperty;

class DoublePropertyMapper
{
    static JAXBElement<XmlDoubleProperty> map( final Property property, final ObjectFactory objectFactory )
    {
        XmlDoubleProperty prop = new XmlDoubleProperty();
        prop.setName( property.getName() );
        prop.setValue( property.getDouble() );

        return objectFactory.createXmlPropertyTreeDouble( prop );
    }

}
