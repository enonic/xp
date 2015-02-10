package com.enonic.wem.export.internal.xml.mapper;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.export.internal.xml.XmlStringProperty;
import com.enonic.wem.export.internal.xml.util.XmlStringEscaper;

class StringPropertyMapper
{
    static XmlStringProperty map( final Property property )
    {
        XmlStringProperty prop = new XmlStringProperty();
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
