package com.enonic.wem.api.xml.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "node")
@XmlRootElement(name = "node")
public final class XmlNode
{
    @XmlElement
    private String id;

    @XmlElement
    private XmlDataSet data;

    public String getId()
    {
        return id;
    }

    public void setId( String value )
    {
        this.id = value;
    }

    public XmlDataSet getData()
    {
        return data;
    }

    public void setData( XmlDataSet value )
    {
        this.data = value;
    }
}
