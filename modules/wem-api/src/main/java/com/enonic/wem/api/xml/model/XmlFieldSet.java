package com.enonic.wem.api.xml.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "fieldSet")
public final class XmlFieldSet
    extends XmlFormItem
{
    @XmlElement
    private String label;

    @XmlElement
    private XmlFormItems items;

    public String getLabel()
    {
        return label;
    }

    public void setLabel( String value )
    {
        this.label = value;
    }

    public XmlFormItems getItems()
    {
        return items;
    }

    public void setItems( XmlFormItems value )
    {
        this.items = value;
    }
}
