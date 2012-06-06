package com.enonic.wem.web.rest.account;

import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;

public class AddressModel
{
    public static final String LABEL = "label";

    public static final String STREET = "street";

    public static final String POSTAL_ADDRESS = "postalAddress";

    public static final String POSTAL_CODE = "postalCode";

    public static final String REGION = "region";

    public static final String COUNTRY = "country";

    public static final String ISO_REGION = "isoRegion";

    public static final String ISO_COUNTRY = "isoCountry";

    private String label;

    private String street;

    private String postalAddress;

    private String postalCode;

    private String region;

    private String country;

    private String isoRegion;

    private String isoCountry;

    public AddressModel()
    {
    }

    public AddressModel( Map<String, Object> addressData )
    {
        this.label = getAddressField( addressData, LABEL );
        this.street = getAddressField( addressData, STREET );
        this.postalAddress = getAddressField( addressData, POSTAL_ADDRESS );
        this.postalCode = getAddressField( addressData, POSTAL_CODE );
        this.region = getAddressField( addressData, REGION );
        this.country = getAddressField( addressData, COUNTRY );
        this.isoCountry = getAddressField( addressData, ISO_COUNTRY );
        this.isoRegion = getAddressField( addressData, ISO_REGION );
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

    @JsonProperty(POSTAL_ADDRESS)
    public String getPostalAddress()
    {
        return postalAddress;
    }

    public void setPostalAddress( String postalAddress )
    {
        this.postalAddress = postalAddress;
    }

    @JsonProperty(POSTAL_CODE)
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

    @JsonProperty(ISO_REGION)
    public String getIsoRegion()
    {
        return isoRegion;
    }

    public void setIsoRegion( String isoRegion )
    {
        this.isoRegion = isoRegion;
    }

    @JsonProperty(ISO_COUNTRY)
    public String getIsoCountry()
    {
        return isoCountry;
    }

    public void setIsoCountry( String isoCountry )
    {
        this.isoCountry = isoCountry;
    }

    public String getAddressField( Map<String, Object> addressData, String fieldName )
    {
        return addressData.get( fieldName ) != null ? addressData.get( fieldName ).toString() : null;
    }
}
