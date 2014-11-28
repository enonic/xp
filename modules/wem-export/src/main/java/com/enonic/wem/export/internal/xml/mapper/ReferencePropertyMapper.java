package com.enonic.wem.export.internal.xml.mapper;

import javax.xml.bind.JAXBElement;

import com.enonic.wem.api.data2.Property;
import com.enonic.wem.export.internal.xml.ObjectFactory;
import com.enonic.wem.export.internal.xml.XmlReferenceProperty;

class ReferencePropertyMapper
{
    static JAXBElement<XmlReferenceProperty> map( final Property property, final ObjectFactory objectFactory )
    {
        XmlReferenceProperty prop = new XmlReferenceProperty();
        prop.setName( property.getName() );
        prop.setValue( property.getString() );

        return objectFactory.createXmlPropertyTreeReference( prop );
    }

}
