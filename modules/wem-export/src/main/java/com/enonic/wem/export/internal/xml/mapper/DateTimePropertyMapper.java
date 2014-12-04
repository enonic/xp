package com.enonic.wem.export.internal.xml.mapper;

import javax.xml.bind.JAXBElement;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.export.internal.xml.ObjectFactory;
import com.enonic.wem.export.internal.xml.XmlDateTimeProperty;
import com.enonic.wem.export.internal.xml.util.XmlDateTimeConverter;

class DateTimePropertyMapper
{
    static JAXBElement<XmlDateTimeProperty> map( final Property property, final ObjectFactory objectFactory )
    {
        XmlDateTimeProperty prop = new XmlDateTimeProperty();
        prop.setName( property.getName() );
        prop.setValue( XmlDateTimeConverter.toXMLGregorianCalendar( property.getInstant() ) );

        return objectFactory.createXmlPropertyTreeDateTime( prop );
    }

}
