package com.enonic.xp.core.impl.export.xml.mapper;

import com.enonic.xp.core.data.Property;
import com.enonic.xp.core.impl.export.xml.XmlLinkProperty;
import com.enonic.xp.core.impl.export.xml.util.XmlStringEscaper;

class LinkPropertyMapper
{
    static XmlLinkProperty map( final Property property )
    {
        XmlLinkProperty prop = new XmlLinkProperty();
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
