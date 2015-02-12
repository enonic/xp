package com.enonic.wem.export.internal.xml.mapper;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.export.internal.xml.XmlLinkProperty;
import com.enonic.wem.export.internal.xml.util.XmlStringEscaper;

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
