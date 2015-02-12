package com.enonic.wem.export.internal.xml.mapper;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.export.internal.xml.XmlReferenceProperty;
import com.enonic.wem.export.internal.xml.util.XmlStringEscaper;

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
