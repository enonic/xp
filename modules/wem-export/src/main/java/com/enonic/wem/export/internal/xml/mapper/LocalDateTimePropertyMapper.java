package com.enonic.wem.export.internal.xml.mapper;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.export.internal.xml.XmlLocalDateTimeProperty;
import com.enonic.wem.export.internal.xml.util.XmlDateTimeConverter;

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
