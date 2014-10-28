package com.enonic.wem.api.xml.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "layoutDescriptor")
@XmlRootElement(name = "layout-component")
public final class XmlLayoutDescriptor
{
    @XmlElement(name = "display-name")
    private String displayName;

    @XmlElement
    private XmlForm config;

    @XmlElement
    private XmlRegionsDescriptor regions;

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

    public XmlRegionsDescriptor getRegions()
    {
        return regions;
    }

    public void setRegions( XmlRegionsDescriptor value )
    {
        this.regions = value;
    }
}
