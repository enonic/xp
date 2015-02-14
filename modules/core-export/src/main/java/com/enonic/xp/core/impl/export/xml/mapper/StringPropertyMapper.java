package com.enonic.xp.core.impl.export.xml.mapper;

import com.enonic.xp.core.data.Property;
import com.enonic.xp.core.impl.export.xml.XmlStringProperty;
import com.enonic.xp.core.impl.export.xml.util.XmlStringEscaper;

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
