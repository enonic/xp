package com.enonic.wem.export.internal.xml.mapper;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.export.internal.xml.XmlDoubleProperty;

class DoublePropertyMapper
{
    static XmlDoubleProperty map( final Property property )
    {
        XmlDoubleProperty prop = new XmlDoubleProperty();
        prop.setName( property.getName() );
        prop.setValue( property.getDouble() );

        return prop;
    }
}
