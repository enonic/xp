package com.enonic.xp.core.impl.export.xml.mapper;

import com.enonic.xp.data.Property;
import com.enonic.xp.core.impl.export.xml.XmlDateTimeProperty;
import com.enonic.xp.core.impl.export.xml.util.XmlDateTimeConverter;

class DateTimePropertyMapper
{
    static XmlDateTimeProperty map( final Property property )
    {
        XmlDateTimeProperty prop = new XmlDateTimeProperty();
        prop.setName( property.getName() );
        prop.setValue( XmlDateTimeConverter.toXMLGregorianCalendar( property.getInstant() ) );

        return prop;
    }
}
