package com.enonic.xp.core.impl.export.xml.mapper;

import com.enonic.xp.core.data.Property;
import com.enonic.xp.core.impl.export.xml.XmlTimeProperty;
import com.enonic.xp.core.impl.export.xml.util.XmlDateTimeConverter;

class TimePropertyMapper
{
    static XmlTimeProperty map( final Property property )
    {
        XmlTimeProperty prop = new XmlTimeProperty();
        prop.setName( property.getName() );
        prop.setValue( XmlDateTimeConverter.toXMLGregorianCalendar( property.getLocalTime() ) );

        return prop;
    }

}
