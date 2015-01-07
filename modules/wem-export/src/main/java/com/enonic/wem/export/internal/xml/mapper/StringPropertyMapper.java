package com.enonic.wem.export.internal.xml.mapper;

import javax.xml.bind.JAXBElement;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.export.internal.xml.ObjectFactory;
import com.enonic.wem.export.internal.xml.XmlStringProperty;

class StringPropertyMapper
{
    static JAXBElement<XmlStringProperty> map( final Property property, final ObjectFactory objectFactory )
    {
        XmlStringProperty prop = new XmlStringProperty();
        prop.setName( property.getName() );

        if ( property.hasNullValue() )
        {
            prop.setIsNull( true );
        }
        else
        {
            prop.setValue( property.getString() );
        }

        return objectFactory.createXmlPropertyTreeString( prop );
    }

}
