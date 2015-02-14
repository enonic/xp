package com.enonic.xp.core.impl.export.xml.mapper;

import com.enonic.xp.core.data.Property;
import com.enonic.xp.core.impl.export.xml.XmlGeoPointProperty;
import com.enonic.xp.core.impl.export.xml.util.XmlStringEscaper;

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
