package com.enonic.wem.api.xml.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "node")
@XmlRootElement(name = "node")
public final class XmlNode
{
    @XmlElement
    private String id;

    @XmlElement(name = "created-time")
    @XmlSchemaType(name = "dateTime")
    private XMLGregorianCalendar createdTime;

    @XmlElement(name = "modified-time")
    @XmlSchemaType(name = "dateTime")
    private XMLGregorianCalendar modifiedTime;

    @XmlElement
    private String creator;

    @XmlElement
    private String modifier;

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

    public XMLGregorianCalendar getCreatedTime()
    {
        return createdTime;
    }

    public void setCreatedTime( XMLGregorianCalendar value )
    {
        this.createdTime = value;
    }

    public XMLGregorianCalendar getModifiedTime()
    {
        return modifiedTime;
    }

    public void setModifiedTime( XMLGregorianCalendar value )
    {
        this.modifiedTime = value;
    }

    public String getCreator()
    {
        return creator;
    }

    public void setCreator( String value )
    {
        this.creator = value;
    }

    public String getModifier()
    {
        return modifier;
    }

    public void setModifier( String value )
    {
        this.modifier = value;
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
