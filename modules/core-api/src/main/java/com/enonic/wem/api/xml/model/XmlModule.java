package com.enonic.wem.api.xml.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "module")
@XmlRootElement(name = "module")
public final class XmlModule
{
    @XmlElement(required = true)
    private String name;

    @XmlElement(required = true)
    private String version;

    @XmlElement(name = "display-name")
    private String displayName;

    @XmlElement
    private String url;

    @XmlElement
    private XmlVendor vendor;

    @XmlElement(name = "content")
    private XmlModuleMetaSteps metaSteps;

    @XmlElement
    private XmlForm config;

    public String getName()
    {
        return name;
    }

    public void setName( String value )
    {
        this.name = value;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion( String value )
    {
        this.version = value;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName( String value )
    {
        this.displayName = value;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl( String value )
    {
        this.url = value;
    }

    public XmlVendor getVendor()
    {
        return vendor;
    }

    public void setVendor( XmlVendor value )
    {
        this.vendor = value;
    }

    public XmlForm getConfig()
    {
        return config;
    }

    public void setConfig( XmlForm value )
    {
        this.config = value;
    }

    public XmlModuleMetaSteps getMetaSteps()
    {
        return metaSteps;
    }

    public void setMetaSteps( XmlModuleMetaSteps value )
    {
        this.metaSteps = value;
    }
}
