package com.enonic.wem.api.xml.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "partDescriptor")
public final class XmlPartDescriptor
{
    @XmlElement(name = "display-name")
    private String displayName;

    @XmlElement
    private XmlForm config;

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName( String value )
    {
        this.displayName = value;
    }

    public XmlForm getConfig()
    {
        return config;
    }

    public void setConfig( XmlForm value )
    {
        this.config = value;
    }
}
