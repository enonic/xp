package com.enonic.wem.api.xml.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "formItemSet")
public final class XmlFormItemSet
    extends XmlFormItem
{
    @XmlElement
    private String label;

    @XmlElement
    private Boolean immutable;

    @XmlElement(name = "custom-text")
    private String customText;

    @XmlElement(name = "help-text")
    private String helpText;

    @XmlElement
    private XmlOccurrence occurrences;

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

    public Boolean isImmutable()
    {
        return immutable;
    }

    public void setImmutable( Boolean value )
    {
        this.immutable = value;
    }

    public String getCustomText()
    {
        return customText;
    }

    public void setCustomText( String value )
    {
        this.customText = value;
    }

    public String getHelpText()
    {
        return helpText;
    }

    public void setHelpText( String value )
    {
        this.helpText = value;
    }

    public XmlOccurrence getOccurrences()
    {
        return occurrences;
    }

    public void setOccurrences( XmlOccurrence value )
    {
        this.occurrences = value;
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
