package com.enonic.wem.export.internal.xml.mapper;

import javax.xml.bind.JAXBElement;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.export.internal.xml.ObjectFactory;
import com.enonic.wem.export.internal.xml.XmlBooleanProperty;

class BooleanPropertyMapper
{
    static JAXBElement<XmlBooleanProperty> map( final Property property, final ObjectFactory objectFactory )
    {
        XmlBooleanProperty prop = new XmlBooleanProperty();
        prop.setName( property.getName() );
        prop.setValue( property.getBoolean() );

        return objectFactory.createXmlPropertyTreeBoolean( prop );
    }
}
