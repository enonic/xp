package com.enonic.wem.export.internal.xml.mapper;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.export.internal.xml.XmlTimeProperty;
import com.enonic.wem.export.internal.xml.util.XmlDateTimeConverter;

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
