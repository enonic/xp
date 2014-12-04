package com.enonic.wem.export.internal.xml.mapper;

import javax.xml.bind.JAXBElement;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.export.internal.xml.ObjectFactory;
import com.enonic.wem.export.internal.xml.XmlTimeProperty;
import com.enonic.wem.export.internal.xml.util.XmlDateTimeConverter;

class TimePropertyMapper
{
    static JAXBElement<XmlTimeProperty> map( final Property property, final ObjectFactory objectFactory )
    {
        XmlTimeProperty prop = new XmlTimeProperty();
        prop.setName( property.getName() );
        prop.setValue( XmlDateTimeConverter.toXMLGregorianCalendar( property.getLocalTime() ) );

        return objectFactory.createXmlPropertyTreeLocalTime( prop );
    }

}
