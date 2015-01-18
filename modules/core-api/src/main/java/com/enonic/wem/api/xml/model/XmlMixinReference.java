package com.enonic.wem.api.xml.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "mixinReference")
public final class XmlMixinReference
    extends XmlFormItem
{
    @XmlElement(required = true)
    private String reference;

    public String getReference()
    {
        return reference;
    }

    public void setReference( String value )
    {
        this.reference = value;
    }
}
