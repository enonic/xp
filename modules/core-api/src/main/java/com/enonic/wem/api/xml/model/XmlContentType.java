package com.enonic.wem.api.xml.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "contentType")
@XmlRootElement(name = "content-type")
public final class XmlContentType
{
    @XmlElement(name = "display-name")
    private String displayName;

    @XmlElement
    private String description;

    @XmlElement(name = "content-display-name-script")
    private String contentDisplayNameScript;

    @XmlElement(name = "super-type")
    private String superType;

    @XmlElement(name = "is-abstract")
    private Boolean isAbstract;

    @XmlElement(name = "is-final")
    private Boolean isFinal;

    @XmlElement(name = "is-built-in")
    private Boolean isBuiltIn;

    @XmlElement(name = "allow-child-content")
    private Boolean allowChildContent;

    @XmlElement
    private XmlFormItems form;

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName( String value )
    {
        this.displayName = value;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String value )
    {
        this.description = value;
    }

    public String getContentDisplayNameScript()
    {
        return contentDisplayNameScript;
    }

    public void setContentDisplayNameScript( String value )
    {
        this.contentDisplayNameScript = value;
    }

    public String getSuperType()
    {
        return superType;
    }

    public void setSuperType( String value )
    {
        this.superType = value;
    }

    public Boolean isIsAbstract()
    {
        return isAbstract;
    }

    public void setIsAbstract( Boolean value )
    {
        this.isAbstract = value;
    }

    public Boolean isIsFinal()
    {
        return isFinal;
    }

    public void setIsFinal( Boolean value )
    {
        this.isFinal = value;
    }

    public Boolean isIsBuiltIn()
    {
        return isBuiltIn;
    }

    public void setIsBuiltIn( Boolean value )
    {
        this.isBuiltIn = value;
    }

    public Boolean isAllowChildContent()
    {
        return allowChildContent;
    }

    public void setAllowChildContent( Boolean value )
    {
        this.allowChildContent = value;
    }

    public XmlFormItems getForm()
    {
        return form;
    }

    public void setForm( XmlFormItems value )
    {
        this.form = value;
    }
}
