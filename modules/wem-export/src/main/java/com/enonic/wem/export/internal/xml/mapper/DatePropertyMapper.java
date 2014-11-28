package com.enonic.wem.export.internal.xml.mapper;

import javax.xml.bind.JAXBElement;

import com.enonic.wem.api.data2.Property;
import com.enonic.wem.export.internal.xml.ObjectFactory;
import com.enonic.wem.export.internal.xml.XmlDateProperty;
import com.enonic.wem.export.internal.xml.util.InstantConverter;

class DatePropertyMapper
{
    static JAXBElement<XmlDateProperty> map( final Property property, final ObjectFactory objectFactory )
    {
        XmlDateProperty prop = new XmlDateProperty();
        prop.setName( property.getName() );
        prop.setValue( InstantConverter.convertToXmlSerializable( property.getLocalDate() ) );

        return objectFactory.createXmlPropertyTreeLocalDate( prop );
    }

}
