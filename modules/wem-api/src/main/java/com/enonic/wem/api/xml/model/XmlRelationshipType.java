package com.enonic.wem.api.xml.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "relationshipType")
@XmlRootElement(name = "relationship-type")
public final class XmlRelationshipType
{
    @XmlElement
    private String description;

    @XmlElement(name = "from-semantic")
    private String fromSemantic;

    @XmlElement(name = "to-semantic")
    private String toSemantic;

    @XmlElement(name = "content-type")
    @XmlElementWrapper(name = "allowed-from-types")
    private List<String> allowedFromTypes;

    @XmlElement(name = "content-type")
    @XmlElementWrapper(name = "allowed-to-types")
    private List<String> allowedToTypes;

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String value )
    {
        this.description = value;
    }

    public String getFromSemantic()
    {
        return fromSemantic;
    }

    public void setFromSemantic( String value )
    {
        this.fromSemantic = value;
    }

    public String getToSemantic()
    {
        return toSemantic;
    }

    public void setToSemantic( String value )
    {
        this.toSemantic = value;
    }

    public List<String> getAllowedFromTypes()
    {
        return allowedFromTypes;
    }

    public void setAllowedFromTypes( List<String> value )
    {
        this.allowedFromTypes = value;
    }

    public List<String> getAllowedToTypes()
    {
        return allowedToTypes;
    }

    public void setAllowedToTypes( List<String> value )
    {
        this.allowedToTypes = value;
    }
}
