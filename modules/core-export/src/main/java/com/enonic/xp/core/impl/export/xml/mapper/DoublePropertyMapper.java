package com.enonic.xp.core.impl.export.xml.mapper;

import com.enonic.xp.data.Property;
import com.enonic.xp.core.impl.export.xml.XmlDoubleProperty;

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
