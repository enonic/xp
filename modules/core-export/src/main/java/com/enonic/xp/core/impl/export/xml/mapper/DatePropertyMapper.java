package com.enonic.xp.core.impl.export.xml.mapper;

import com.enonic.xp.core.data.Property;
import com.enonic.xp.core.impl.export.xml.XmlDateProperty;
import com.enonic.xp.core.impl.export.xml.util.XmlDateTimeConverter;

class DatePropertyMapper
{
    static XmlDateProperty map( final Property property )
    {
        XmlDateProperty prop = new XmlDateProperty();
        prop.setName( property.getName() );
        prop.setValue( XmlDateTimeConverter.toXMLGregorianCalendar( property.getLocalDate() ) );

        return prop;
    }
}
