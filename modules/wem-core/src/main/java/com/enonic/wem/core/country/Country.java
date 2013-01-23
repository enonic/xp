package com.enonic.wem.core.country;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.base.Preconditions;

public final class Country
{
    private CountryCode code;

    private String englishName;

    private String localName;

    private String regionsEnglishName;

    private String regionsLocalName;

    private Map<String, Region> regionsMapByCode = new LinkedHashMap<String, Region>();

    private String callingCode;

    public Country( final CountryCode code, final String englishName, final String localName, final String callingCode )
    {
        Preconditions.checkNotNull(code, "code cannot be null");
        this.code = code;
        this.englishName = englishName;
        this.localName = localName;
        this.callingCode = callingCode;
    }

    public CountryCode getCode()
    {
        return code;
    }

    public String getEnglishName()
    {
        return englishName;
    }

    public String getLocalName()
    {
        return localName;
    }

    public String getRegionsLocalName()
    {
        return regionsLocalName;
    }

    public void setRegionsLocalName( String regionsLocalName )
    {
        this.regionsLocalName = regionsLocalName;
    }

    public String getRegionsEnglishName()
    {
        return regionsEnglishName;
    }

    public void setRegionsEnglishName( String regionsEnglishName )
    {
        this.regionsEnglishName = regionsEnglishName;
    }

    public void addRegion( Region region )
    {
        Preconditions.checkNotNull(region, "region cannot be null");
        regionsMapByCode.put( region.getCode(), region );
    }

    public Region getRegion( String code )
    {
        Preconditions.checkNotNull(code, "code cannot be null");
        return regionsMapByCode.get( code );
    }

    public Collection<Region> getRegions()
    {
        return regionsMapByCode.values();
    }

    public boolean hasRegions()
    {
        return !regionsMapByCode.isEmpty();
    }

    public String getCallingCode()
    {
        return callingCode;
    }

}
