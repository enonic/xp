package com.enonic.wem.xml.data;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.type.ValueType;
import com.enonic.wem.api.data.type.ValueTypes;
import com.enonic.wem.xml.XmlObject;

@XmlRootElement(name = "property")
public final class PropertyXml
    implements XmlObject<Property, DataSet>,DataXml
{
    @XmlAttribute(name = "name", required = true)
    private String name;

    @XmlAttribute(name = "type", required = true)
    private String type;

    @XmlValue
    private String value;

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public void from( final Property property )
    {
        this.name = property.getName();
        this.type = property.getValueType().getName();
        this.value = property.getValue().asString();
    }

    @Override
    public void to( final DataSet output )
    {
        final ValueType valueType = ValueTypes.parseByName( this.type );
        final Property property = valueType.newProperty( this.name, this.value );
        output.add(property);
    }
}
