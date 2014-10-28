package com.enonic.wem.api.xml.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "dataProperty")
public final class XmlDataProperty
{
    @XmlValue
    private String value;

    @XmlAttribute(name = "name", required = true)
    private String name;

    @XmlAttribute(name = "type", required = true)
    private XmlDataPropertyType type;

    public String getValue()
    {
        return value;
    }

    public void setValue( String value )
    {
        this.value = value;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String value )
    {
        this.name = value;
    }

    public XmlDataPropertyType getType()
    {
        return type;
    }

    public void setType( XmlDataPropertyType value )
    {
        this.type = value;
    }
}
