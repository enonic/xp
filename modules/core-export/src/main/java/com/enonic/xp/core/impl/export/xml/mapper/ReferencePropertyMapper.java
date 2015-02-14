package com.enonic.xp.core.impl.export.xml.mapper;

import com.enonic.xp.core.data.Property;
import com.enonic.xp.core.impl.export.xml.XmlReferenceProperty;
import com.enonic.xp.core.impl.export.xml.util.XmlStringEscaper;

class ReferencePropertyMapper
{
    static XmlReferenceProperty map( final Property property )
    {
        XmlReferenceProperty prop = new XmlReferenceProperty();
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
