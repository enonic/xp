package com.enonic.wem.web.rest.country;


public final class RegionModel
{
    private String countryCode;

    private String regionCode;

    private String englishName;

    private String localName;


    public String getCountryCode()
    {
        return countryCode;
    }

    public void setCountryCode( String countryCode )
    {
        this.countryCode = countryCode;
    }

    public String getRegionCode()
    {
        return regionCode;
    }

    public void setRegionCode( String regionCode )
    {
        this.regionCode = regionCode;
    }

    public String getEnglishName()
    {
        return englishName;
    }

    public void setEnglishName( String englishName )
    {
        this.englishName = englishName;
    }

    public String getLocalName()
    {
        return localName;
    }

    public void setLocalName( String localName )
    {
        this.localName = localName;
    }
}
