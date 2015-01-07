package com.enonic.wem.export.internal.xml.mapper;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.export.internal.xml.XmlLongProperty;

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
