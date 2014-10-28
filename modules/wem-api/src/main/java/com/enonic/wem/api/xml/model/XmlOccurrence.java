package com.enonic.wem.api.xml.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "occurrence")
public final class XmlOccurrence
{
    @XmlAttribute(name = "minimum", required = true)
    private int minimum;

    @XmlAttribute(name = "maximum", required = true)
    private int maximum;

    public int getMinimum()
    {
        return minimum;
    }

    public void setMinimum( int value )
    {
        this.minimum = value;
    }

    public int getMaximum()
    {
        return maximum;
    }

    public void setMaximum( int value )
    {
        this.maximum = value;
    }
}
