package com.enonic.wem.export.internal.xml.mapper;

import javax.xml.bind.JAXBElement;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.export.internal.xml.ObjectFactory;
import com.enonic.wem.export.internal.xml.XmlGeoPointProperty;

class GeoPointPropertyMapper
{
    static JAXBElement<XmlGeoPointProperty> map( final Property property, final ObjectFactory objectFactory )
    {
        XmlGeoPointProperty prop = new XmlGeoPointProperty();
        prop.setName( property.getName() );
        prop.setValue( property.getGeoPoint().toString() );

        return objectFactory.createXmlPropertyTreeGeoPoint( prop );
    }

}
