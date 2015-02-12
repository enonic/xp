package com.enonic.wem.export.internal.xml.mapper;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.export.internal.xml.XmlGeoPointProperty;
import com.enonic.wem.export.internal.xml.util.XmlStringEscaper;

class GeoPointPropertyMapper
{
    static XmlGeoPointProperty map( final Property property )
    {
        XmlGeoPointProperty prop = new XmlGeoPointProperty();
        prop.setName( property.getName() );

        if ( property.hasNullValue() )
        {
            prop.setIsNull( true );
        }
        else
        {
            prop.setValue( XmlStringEscaper.escapeContent( property.getString() ) );
        }

        return prop;
    }
}
