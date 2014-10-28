package com.enonic.wem.api.xml.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "metadataSchema")
@XmlRootElement(name = "metadata")
public final class XmlMetadataSchema
{
    @XmlElement
    private XmlForm form;

    @XmlAttribute(name = "name", required = true)
    private String name;

    public XmlForm getForm()
    {
        return form;
    }

    public void setForm( XmlForm value )
    {
        this.form = value;
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

