package com.enonic.wem.api.xml.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "mixin")
@XmlRootElement(name = "mixin")
public final class XmlMixin
{
    @XmlElement(name = "display-name")
    private String displayName;

    @XmlElement
    private String description;

    @XmlElement
    private XmlFormItems items;

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName( String value )
    {
        this.displayName = value;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String value )
    {
        this.description = value;
    }

    public XmlFormItems getItems()
    {
        return items;
    }

    public void setItems( XmlFormItems value )
    {
        this.items = value;
    }
}
