package com.enonic.wem.api.xml.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "partComponent")
public final class XmlPartComponent
{
    @XmlElement(name = "display-name")
    private String displayName;

    @XmlElement
    private Object config;

    @XmlAttribute(name = "descriptor")
    private String descriptor;

    @XmlAttribute(name = "name")
    private String name;

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName( String value )
    {
        this.displayName = value;
    }

    public Object getConfig()
    {
        return config;
    }

    public void setConfig( Object value )
    {
        this.config = value;
    }

    public String getDescriptor()
    {
        return descriptor;
    }

    public void setDescriptor( String value )
    {
        this.descriptor = value;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String value )
    {
        this.name = value;
    }
}
