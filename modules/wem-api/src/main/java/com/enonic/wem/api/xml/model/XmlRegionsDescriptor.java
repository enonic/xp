package com.enonic.wem.api.xml.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.collect.Lists;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "regionsDescriptor")
public final class XmlRegionsDescriptor
{
    @XmlElement(name = "region")
    private List<XmlRegionDescriptor> list = Lists.newArrayList();

    public List<XmlRegionDescriptor> getList()
    {
        return this.list;
    }
}
