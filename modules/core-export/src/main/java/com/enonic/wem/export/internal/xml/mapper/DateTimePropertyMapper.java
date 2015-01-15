package com.enonic.wem.export.internal.xml.mapper;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.export.internal.xml.XmlDateTimeProperty;
import com.enonic.wem.export.internal.xml.util.XmlDateTimeConverter;

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
