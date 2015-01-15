package com.enonic.wem.export.internal.xml.mapper;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.export.internal.xml.XmlXmlProperty;
import com.enonic.wem.export.internal.xml.util.XmlStringEscaper;

class XmlValuePropertyMapper
{
    static XmlXmlProperty map( final Property property )
    {
        XmlXmlProperty prop = new XmlXmlProperty();
        prop.setName( property.getName() );

        if ( property.hasNullValue() )
        {
            prop.setIsNull( true );
        }
        else
        {
            prop.setValue( XmlStringEscaper.escapeContent( property.getString() ) );
        }

        return prop;
    }

}
