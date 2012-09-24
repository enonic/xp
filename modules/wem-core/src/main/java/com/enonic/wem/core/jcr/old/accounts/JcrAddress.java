package com.enonic.wem.core.jcr.old.accounts;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class JcrAddress
{
    private String label;

    private String street;

    private String postalAddress;

    private String postalCode;

    private String region;

    private String country;

    private String isoRegion;

    private String isoCountry;

    public JcrAddress()
    {
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel( String label )
    {
        this.label = label;
    }

    public String getStreet()
    {
        return street;
    }

    public void setStreet( String street )
    {
        this.street = street;
    }

    public String getPostalAddress()
    {
        return postalAddress;
    }

    public void setPostalAddress( String postalAddress )
    {
        this.postalAddress = postalAddress;
    }

    public String getPostalCode()
    {
        return postalCode;
    }

    public void setPostalCode( String postalCode )
    {
        this.postalCode = postalCode;
    }

    public String getRegion()
    {
        return region;
    }

    public void setRegion( String region )
    {
        this.region = region;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry( String country )
    {
        this.country = country;
    }

    public String getIsoRegion()
    {
        return isoRegion;
    }

    public void setIsoRegion( String isoRegion )
    {
        this.isoRegion = isoRegion;
    }

    public String getIsoCountry()
    {
        return isoCountry;
    }

    public void setIsoCountry( String isoCountry )
    {
        this.isoCountry = isoCountry;
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder()
            .append( this.label )
            .append( this.street )
            .append( this.postalAddress )
            .append( this.postalCode )
            .append( this.region )
            .append( this.country )
            .append( this.isoRegion )
            .append( this.isoCountry )
            .toHashCode();
    }

    @Override
    public boolean equals( final Object obj )
    {
        if ( obj == null )
        {
            return false;
        }
        if ( getClass() != obj.getClass() )
        {
            return false;
        }
        final JcrAddress other = (JcrAddress) obj;
        return new EqualsBuilder()
            .append( this.label, other.label )
            .append( this.street, other.street )
            .append( this.postalAddress, other.postalAddress )
            .append( this.postalCode, other.postalCode )
            .append( this.region, other.region )
            .append( this.country, other.country )
            .append( this.isoRegion, other.isoRegion )
            .append( this.isoCountry, other.isoCountry )
            .isEquals();
    }
}
