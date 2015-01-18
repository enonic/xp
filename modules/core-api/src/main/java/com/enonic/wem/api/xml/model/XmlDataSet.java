package com.enonic.wem.api.xml.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;

import com.google.common.collect.Lists;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "dataSet")
public final class XmlDataSet
{
    @XmlElements(
        {@XmlElement(name = "property", type = XmlDataProperty.class), @XmlElement(name = "property-set", type = XmlDataSet.class)})
    private List<Object> properties = Lists.newArrayList();

    @XmlAttribute(name = "name", required = true)
    private String name;

    public List<Object> getProperties()
    {
        return this.properties;
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
