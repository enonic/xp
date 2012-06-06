package com.enonic.wem.web.rest.country;

import java.util.List;

public final class CountryModel
{
    private String code;

    private String englishName;

    private String localName;

    private String regionsEnglishName;

    private String regionsLocalName;

    private List<CallingCodeModel> callingCodes;

    private List<RegionModel> regions;


    public String getCode()
    {
        return code;
    }

    public void setCode( String code )
    {
        this.code = code;
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

    public String getRegionsEnglishName()
    {
        return regionsEnglishName;
    }

    public void setRegionsEnglishName( String regionsEnglishName )
    {
        this.regionsEnglishName = regionsEnglishName;
    }

    public String getRegionsLocalName()
    {
        return regionsLocalName;
    }

    public void setRegionsLocalName( String regionsLocalName )
    {
        this.regionsLocalName = regionsLocalName;
    }

    public List<CallingCodeModel> getCallingCodes()
    {
        return callingCodes;
    }

    public void setCallingCodes( List<CallingCodeModel> callingCodes )
    {
        this.callingCodes = callingCodes;
    }

    public List<RegionModel> getRegions()
    {
        return regions;
    }

    public void setRegions( List<RegionModel> regions )
    {
        this.regions = regions;
    }
}
