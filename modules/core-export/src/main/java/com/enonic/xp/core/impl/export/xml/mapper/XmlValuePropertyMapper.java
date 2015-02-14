package com.enonic.xp.core.impl.export.xml.mapper;

import com.enonic.xp.core.data.Property;
import com.enonic.xp.core.impl.export.xml.XmlXmlProperty;
import com.enonic.xp.core.impl.export.xml.util.XmlStringEscaper;

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
