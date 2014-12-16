package com.enonic.wem.export.internal.xml.mapper;

import javax.xml.bind.JAXBElement;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.export.internal.xml.ObjectFactory;
import com.enonic.wem.export.internal.xml.XmlBinaryReferenceProperty;

class BinaryReferencePropertyMapper
{
    static JAXBElement<XmlBinaryReferenceProperty> map( final Property property, final ObjectFactory objectFactory )
    {
        XmlBinaryReferenceProperty prop = new XmlBinaryReferenceProperty();
        prop.setName( property.getName() );
        prop.setValue( property.getString() );

        return objectFactory.createXmlPropertyTreeBinaryReference( prop );
    }

}
