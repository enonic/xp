package com.enonic.wem.export.internal.xml.mapper;

import javax.xml.bind.JAXBElement;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.export.internal.xml.ObjectFactory;
import com.enonic.wem.export.internal.xml.XmlXmlProperty;
import com.enonic.wem.export.internal.xml.util.XmlStringEscaper;

class XmlValuePropertyMapper
{
    static JAXBElement<XmlXmlProperty> map( final Property property, final ObjectFactory objectFactory )
    {
        XmlXmlProperty prop = new XmlXmlProperty();
        prop.setName( property.getName() );
        prop.setValue( XmlStringEscaper.escapeContent( property.getString() ) );

        return objectFactory.createXmlPropertyTreeXml( prop );
    }

}
