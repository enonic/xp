package com.enonic.wem.export.internal.xml.mapper;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.export.internal.xml.XmlHtmlPartProperty;
import com.enonic.wem.export.internal.xml.util.XmlStringEscaper;

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
