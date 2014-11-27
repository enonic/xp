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

    @XmlElement
    private String creator;

    @XmlElement
    private String modifier;

    @XmlElement
    private String name;

    @XmlElement
    private String parent;

    @XmlElement
    @XmlSchemaType(name = "dateTime")
    private XMLGregorianCalendar createdTime;

    @XmlElement
    @XmlSchemaType(name = "dateTime")
    private XMLGregorianCalendar modifiedTime;

    @XmlElement
    private String childOrder;

    @XmlElement
    private XmlPropertyTree propertyTree;

    public String getId()
    {
        return id;
    }

    public void setId( String value )
    {
        this.id = value;
    }

    public String getCreator()
    {
        return creator;
    }

    public void setCreator( final String creator )
    {
        this.creator = creator;
    }

    public String getModifier()
    {
        return modifier;
    }

    public void setModifier( final String modifier )
    {
        this.modifier = modifier;
    }

    public String getName()
    {
        return name;
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    public String getParent()
    {
        return parent;
    }

    public void setParent( final String parent )
    {
        this.parent = parent;
    }

    public XMLGregorianCalendar getCreatedTime()
    {
        return createdTime;
    }

    public void setCreatedTime( final XMLGregorianCalendar createdTime )
    {
        this.createdTime = createdTime;
    }

    public XMLGregorianCalendar getModifiedTime()
    {
        return modifiedTime;
    }

    public void setModifiedTime( final XMLGregorianCalendar modifiedTime )
    {
        this.modifiedTime = modifiedTime;
    }

    public String getChildOrder()
    {
        return childOrder;
    }

    public void setChildOrder( final String childOrder )
    {
        this.childOrder = childOrder;
    }
}
