package com.enonic.wem.api.xml.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "input")
public final class XmlInput
    extends XmlFormItem
{
    @XmlElement
    private String label;

    @XmlElement
    private Boolean immutable;

    @XmlElement
    private Boolean indexed;

    @XmlElement(name = "custom-text")
    private String customText;

    @XmlElement(name = "help-text")
    private String helpText;

    @XmlElement(name = "validation-regexp")
    private String validationRegexp;

    @XmlElement
    private XmlOccurrence occurrences;

    @XmlElement
    private Object config;

    @XmlAttribute(name = "type", required = true)
    private String type;

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

    public Boolean isIndexed()
    {
        return indexed;
    }

    public void setIndexed( Boolean value )
    {
        this.indexed = value;
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

    public String getValidationRegexp()
    {
        return validationRegexp;
    }

    public void setValidationRegexp( String value )
    {
        this.validationRegexp = value;
    }

    public XmlOccurrence getOccurrences()
    {
        return occurrences;
    }

    public void setOccurrences( XmlOccurrence value )
    {
        this.occurrences = value;
    }

    public Object getConfig()
    {
        return config;
    }

    public void setConfig( Object value )
    {
        this.config = value;
    }

    public String getType()
    {
        return type;
    }

    public void setType( String value )
    {
        this.type = value;
    }
}
