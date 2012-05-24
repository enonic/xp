package com.enonic.wem.web.rest.country;

/**
 * Calling code model for CallingCodeResource
 */
public class CallingCodeModel
{
    private String countryCode;

    private String localName;

    private String englishName;

    private String callingCode;

    public String getCountryCode()
    {
        return countryCode;
    }

    public void setCountryCode( String countryCode )
    {
        this.countryCode = countryCode;
    }

    public String getLocalName()
    {
        return localName;
    }

    public void setLocalName( String localName )
    {
        this.localName = localName;
    }

    public String getEnglishName()
    {
        return englishName;
    }

    public void setEnglishName( String englishName )
    {
        this.englishName = englishName;
    }

    public String getCallingCode()
    {
        return callingCode;
    }

    public void setCallingCode( String callingCode )
    {
        this.callingCode = callingCode;
    }
}
