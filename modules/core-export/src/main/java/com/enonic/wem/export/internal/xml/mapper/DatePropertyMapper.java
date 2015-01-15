package com.enonic.wem.export.internal.xml.mapper;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.export.internal.xml.XmlDateProperty;
import com.enonic.wem.export.internal.xml.util.XmlDateTimeConverter;

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
