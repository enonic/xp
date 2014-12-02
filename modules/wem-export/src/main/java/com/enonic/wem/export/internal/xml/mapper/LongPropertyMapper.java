package com.enonic.wem.export.internal.xml.mapper;

import javax.xml.bind.JAXBElement;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.export.internal.xml.ObjectFactory;
import com.enonic.wem.export.internal.xml.XmlLongProperty;

class LongPropertyMapper
{
    static JAXBElement<XmlLongProperty> map( final Property property, final ObjectFactory objectFactory )
    {
        XmlLongProperty prop = new XmlLongProperty();
        prop.setName( property.getName() );
        prop.setValue( property.getLong() );

        return objectFactory.createXmlPropertyTreeLong( prop );
    }

}
