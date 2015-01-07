package com.enonic.wem.export.internal.xml.mapper;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.export.internal.xml.XmlLinkProperty;

class LinkPropertyMapper
{
    static XmlLinkProperty map( final Property property )
    {
        XmlLinkProperty prop = new XmlLinkProperty();
        prop.setName( property.getName() );
        prop.setValue( property.getString() );

        return prop;
    }
}
