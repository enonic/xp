package com.enonic.wem.export.internal.xml.mapper;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.export.internal.xml.XmlBinaryReferenceProperty;

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
