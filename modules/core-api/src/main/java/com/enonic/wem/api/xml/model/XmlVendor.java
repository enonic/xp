package com.enonic.wem.api.xml.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "vendor")
public final class XmlVendor
{
    @XmlElement(required = true)
    private String name;

    @XmlElement(required = true)
    private String url;

    public String getName()
    {
        return name;
    }

    public void setName( String value )
    {
        this.name = value;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl( String value )
    {
        this.url = value;
    }
}
