package com.enonic.wem.api.xml.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "imageDescriptor")
@XmlRootElement(name = "image-component")
public final class XmlImageDescriptor
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
