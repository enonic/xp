package com.enonic.xp.core.impl.export.xml.mapper;

import com.enonic.xp.core.data.Property;
import com.enonic.xp.core.impl.export.xml.XmlLocalDateTimeProperty;
import com.enonic.xp.core.impl.export.xml.util.XmlDateTimeConverter;

class LocalDateTimePropertyMapper
{
    static XmlLocalDateTimeProperty map( final Property property )
    {
        XmlLocalDateTimeProperty prop = new XmlLocalDateTimeProperty();
        prop.setName( property.getName() );
        prop.setValue( XmlDateTimeConverter.toXMLGregorianCalendar( property.getLocalDateTime() ) );

        return prop;
    }
}
