package com.enonic.wem.api.account.profile;

public final class Address
{
    private String label;

    private String street;

    private String postalAddress;

    private String postalCode;

    private String region;

    private String country;

    private String isoRegion;

    private String isoCountry;

    public String getLabel()
    {
        return label;
    }

    public void setLabel( final String label )
    {
        this.label = label;
    }

    public String getStreet()
    {
        return street;
    }

    public void setStreet( final String street )
    {
        this.street = street;
    }

    public String getPostalAddress()
    {
        return postalAddress;
    }

    public void setPostalAddress( final String postalAddress )
    {
        this.postalAddress = postalAddress;
    }

    public String getPostalCode()
    {
        return postalCode;
    }

    public void setPostalCode( final String postalCode )
    {
        this.postalCode = postalCode;
    }

    public String getRegion()
    {
        return region;
    }

    public void setRegion( final String region )
    {
        this.region = region;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry( final String country )
    {
        this.country = country;
    }

    public String getIsoRegion()
    {
        return isoRegion;
    }

    public void setIsoRegion( final String isoRegion )
    {
        this.isoRegion = isoRegion;
    }

    public String getIsoCountry()
    {
        return isoCountry;
    }

    public void setIsoCountry( final String isoCountry )
    {
        this.isoCountry = isoCountry;
    }
}
