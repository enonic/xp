package com.enonic.wem.api.xml.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "metaStep")
public final class XmlModuleMetaStep
{
    @XmlAttribute(name = "mixin", required = true)
    private String mixinName;

    public String getMixinName()
    {
        return mixinName;
    }

    public void setMixinName( String value )
    {
        this.mixinName = value;
    }
}

