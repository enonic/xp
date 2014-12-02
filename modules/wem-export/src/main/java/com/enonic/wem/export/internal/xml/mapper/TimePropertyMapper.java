package com.enonic.wem.export.internal.xml.mapper;

import javax.xml.bind.JAXBElement;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.export.internal.xml.ObjectFactory;
import com.enonic.wem.export.internal.xml.XmlTimeProperty;
import com.enonic.wem.export.internal.xml.util.InstantConverter;

class TimePropertyMapper
{
    static JAXBElement<XmlTimeProperty> map( final Property property, final ObjectFactory objectFactory )
    {
        XmlTimeProperty prop = new XmlTimeProperty();
        prop.setName( property.getName() );
        prop.setValue( InstantConverter.convertToXmlSerializable( property.getLocalTime() ) );

        return objectFactory.createXmlPropertyTreeLocalTime( prop );
    }

}
