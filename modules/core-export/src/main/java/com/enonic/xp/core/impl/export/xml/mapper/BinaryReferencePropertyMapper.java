package com.enonic.xp.core.impl.export.xml.mapper;

import com.enonic.xp.data.Property;
import com.enonic.xp.core.impl.export.xml.XmlBinaryReferenceProperty;

class BinaryReferencePropertyMapper
{
    static XmlBinaryReferenceProperty map( final Property property )
    {
        XmlBinaryReferenceProperty prop = new XmlBinaryReferenceProperty();
        prop.setName( property.getName() );
        prop.setValue( property.getString() );

        return prop;
    }
}
