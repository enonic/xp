package com.enonic.xp.core.impl.export.xml.mapper;

import com.enonic.wem.api.data.Property;
import com.enonic.xp.core.impl.export.xml.XmlHtmlPartProperty;
import com.enonic.xp.core.impl.export.xml.util.XmlStringEscaper;

class HtmlPartPropertyMapper
{
    static XmlHtmlPartProperty map( final Property property )
    {
        XmlHtmlPartProperty prop = new XmlHtmlPartProperty();
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
