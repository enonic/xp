package com.enonic.wem.migrate.account;

final class OldAddress
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
        return this.label;
    }

    public void setLabel( String label )
    {
        this.label = label;
    }

    public String getStreet()
    {
        return this.street;
    }

    public void setStreet( String street )
    {
        this.street = street;
    }

    public String getPostalCode()
    {
        return this.postalCode;
    }

    public void setPostalCode( String postalCode )
    {
        this.postalCode = postalCode;
    }

    public String getRegion()
    {
        return this.region;
    }

    public void setRegion( String region )
    {
        this.region = region;
    }

    public String getCountry()
    {
        return this.country;
    }

    public void setCountry( String country )
    {
        this.country = country;
    }

    public String getIsoRegion()
    {
        return this.isoRegion;
    }

    public void setIsoRegion( String isoRegion )
    {
        this.isoRegion = isoRegion;
    }

    public String getIsoCountry()
    {
        return this.isoCountry;
    }

    public void setIsoCountry( String isoCountry )
    {
        this.isoCountry = isoCountry;
    }

    public String getPostalAddress()
    {
        return this.postalAddress;
    }

    public void setPostalAddress( String postalAddress )
    {
        this.postalAddress = postalAddress;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        OldAddress address = (OldAddress) o;

        if ( country != null ? !country.equals( address.country ) : address.country != null )
        {
            return false;
        }
        if ( isoCountry != null ? !isoCountry.equals( address.isoCountry ) : address.isoCountry != null )
        {
            return false;
        }
        if ( isoRegion != null ? !isoRegion.equals( address.isoRegion ) : address.isoRegion != null )
        {
            return false;
        }
        if ( label != null ? !label.equals( address.label ) : address.label != null )
        {
            return false;
        }
        if ( postalAddress != null ? !postalAddress.equals( address.postalAddress ) : address.postalAddress != null )
        {
            return false;
        }
        if ( postalCode != null ? !postalCode.equals( address.postalCode ) : address.postalCode != null )
        {
            return false;
        }
        if ( region != null ? !region.equals( address.region ) : address.region != null )
        {
            return false;
        }
        if ( street != null ? !street.equals( address.street ) : address.street != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = label != null ? label.hashCode() : 0;
        result = 31 * result + ( street != null ? street.hashCode() : 0 );
        result = 31 * result + ( postalAddress != null ? postalAddress.hashCode() : 0 );
        result = 31 * result + ( postalCode != null ? postalCode.hashCode() : 0 );
        result = 31 * result + ( region != null ? region.hashCode() : 0 );
        result = 31 * result + ( country != null ? country.hashCode() : 0 );
        result = 31 * result + ( isoRegion != null ? isoRegion.hashCode() : 0 );
        result = 31 * result + ( isoCountry != null ? isoCountry.hashCode() : 0 );
        return result;
    }
}
