package com.enonic.wem.api.xml.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.collect.Lists;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "layoutDescriptor")
@XmlRootElement(name = "layout-component")
public final class XmlLayoutDescriptor
{
    @XmlElement(name = "display-name")
    private String displayName;

    @XmlElement
    private XmlForm config;

    @XmlElement(name = "region")
    @XmlElementWrapper(name = "regions")
    private List<XmlRegionDescriptor> regions = Lists.newArrayList();

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

    public List<XmlRegionDescriptor> getRegions()
    {
        return regions;
    }

    public void setRegions( List<XmlRegionDescriptor> value )
    {
        this.regions = value;
    }
}
