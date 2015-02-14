package com.enonic.xp.core.impl.export.xml.mapper;

import com.enonic.wem.api.data.Property;
import com.enonic.xp.core.impl.export.xml.XmlBooleanProperty;

class BooleanPropertyMapper
{
    static Object map( final Property property )
    {
        XmlBooleanProperty prop = new XmlBooleanProperty();
        prop.setName( property.getName() );
        prop.setValue( property.getBoolean() );

        return prop;
    }
}
