package com.enonic.wem.export.internal.xml.mapper;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.export.internal.xml.XmlGeoPointProperty;

class GeoPointPropertyMapper
{
    static XmlGeoPointProperty map( final Property property )
    {
        XmlGeoPointProperty prop = new XmlGeoPointProperty();
        prop.setName( property.getName() );
        prop.setValue( property.getGeoPoint().toString() );

        return prop;
    }
}
