package com.enonic.wem.export.internal.xml.mapper;

import javax.xml.bind.JAXBElement;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.export.internal.xml.ObjectFactory;
import com.enonic.wem.export.internal.xml.XmlLocalDateTimeProperty;
import com.enonic.wem.export.internal.xml.util.XmlDateTimeConverter;

class LocalDateTimePropertyMapper
{
    static JAXBElement<XmlLocalDateTimeProperty> map( final Property property, final ObjectFactory objectFactory )
    {
        XmlLocalDateTimeProperty prop = new XmlLocalDateTimeProperty();
        prop.setName( property.getName() );
        prop.setValue( XmlDateTimeConverter.toXMLGregorianCalendar( property.getLocalDateTime() ) );

        return objectFactory.createXmlPropertyTreeLocalDateTime( prop );
    }

}
