package com.enonic.wem.api.xml.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "formItem")
public abstract class XmlFormItem
{
    @XmlAttribute(name = "name", required = true)
    private String name;

    public String getName()
    {
        return name;
    }

    public void setName( String value )
    {
        this.name = value;
    }
}
