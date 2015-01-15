package com.enonic.wem.export.internal.xml.mapper;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.export.internal.xml.XmlReferenceProperty;

class ReferencePropertyMapper
{
    static XmlReferenceProperty map( final Property property )
    {
        XmlReferenceProperty prop = new XmlReferenceProperty();
        prop.setName( property.getName() );
        prop.setValue( property.getString() );

        return prop;
    }

}
