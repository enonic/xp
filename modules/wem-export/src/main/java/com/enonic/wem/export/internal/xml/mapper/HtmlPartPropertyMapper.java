package com.enonic.wem.export.internal.xml.mapper;

import javax.xml.bind.JAXBElement;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.export.internal.xml.ObjectFactory;
import com.enonic.wem.export.internal.xml.XmlHtmlPartProperty;
import com.enonic.wem.export.internal.xml.util.XmlStringEscaper;

class HtmlPartPropertyMapper
{
    static JAXBElement<XmlHtmlPartProperty> map( final Property property, final ObjectFactory objectFactory )
    {
        XmlHtmlPartProperty prop = new XmlHtmlPartProperty();
        prop.setName( property.getName() );
        prop.setValue( XmlStringEscaper.escapeContent( property.getString() ) );

        return objectFactory.createXmlPropertyTreeHtmlPart( prop );
    }

}
