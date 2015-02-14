package com.enonic.xp.core.impl.export.xml.mapper;

import com.enonic.wem.api.data.Property;
import com.enonic.xp.core.impl.export.xml.XmlLongProperty;

class LongPropertyMapper
{
    static XmlLongProperty map( final Property property )
    {
        XmlLongProperty prop = new XmlLongProperty();
        prop.setName( property.getName() );
        prop.setValue( property.getLong() );

        return prop;
    }

}
