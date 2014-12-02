package com.enonic.wem.export.internal.xml.mapper;

import javax.xml.bind.JAXBElement;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.export.internal.xml.ObjectFactory;
import com.enonic.wem.export.internal.xml.XmlLinkProperty;

class LinkPropertyMapper
{
    static JAXBElement<XmlLinkProperty> map( final Property property, final ObjectFactory objectFactory )
    {
        XmlLinkProperty prop = new XmlLinkProperty();
        prop.setName( property.getName() );
        prop.setValue( property.getString() );

        return objectFactory.createXmlPropertyTreeLink( prop );
    }

}
