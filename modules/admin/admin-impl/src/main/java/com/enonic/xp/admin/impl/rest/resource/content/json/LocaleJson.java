package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.util.Locale;


public class LocaleJson
{
    private String tag;

    private String displayName;

    private String language;

    private String displayLanguage;

    private String variant;

    private String displayVariant;

    private String country;

    private String displayCountry;

    public LocaleJson( final Locale locale )
    {
        this.tag = locale.toLanguageTag();
        this.displayName = locale.getDisplayName( locale );
        this.language = locale.getLanguage();
        this.displayLanguage = locale.getDisplayLanguage( locale );
        this.variant = locale.getVariant();
        this.displayVariant = locale.getDisplayVariant( locale );
        this.country = locale.getCountry();
        this.displayCountry = locale.getDisplayCountry( locale );
    }

    public String getTag()
    {
        return tag;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getLanguage()
    {
        return language;
    }

    public String getDisplayLanguage()
    {
        return displayLanguage;
    }

    public String getVariant()
    {
        return variant;
    }

    public String getDisplayVariant()
    {
        return displayVariant;
    }

    public String getCountry()
    {
        return country;
    }

    public String getDisplayCountry()
    {
        return displayCountry;
    }
}
