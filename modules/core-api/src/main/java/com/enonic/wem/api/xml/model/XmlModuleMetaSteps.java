package com.enonic.wem.api.xml.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.collect.Lists;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "metaSteps")
public final class XmlModuleMetaSteps
{
    @XmlElement(name = "meta-step")
    private List<XmlModuleMetaStep> metaSteps = Lists.newArrayList();

    public List<XmlModuleMetaStep> getMetaSteps()
    {
        return this.metaSteps;
    }
}
