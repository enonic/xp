package com.enonic.wem.api.xml.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.collect.Lists;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "pageRegion")
public final class XmlPageRegion
{
    @XmlElement(name = "part-component")
    private List<XmlPartComponent> partComponent = Lists.newArrayList();

    @XmlAttribute(name = "name", required = true)
    private String name;

    public List<XmlPartComponent> getPartComponent()
    {
        return this.partComponent;
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
