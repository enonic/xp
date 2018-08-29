package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.util.Locale;
import java.util.Objects;


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

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final LocaleJson that = (LocaleJson) o;
        return Objects.equals( tag, that.tag ) && Objects.equals( displayName, that.displayName ) &&
            Objects.equals( language, that.language ) && Objects.equals( displayLanguage, that.displayLanguage ) &&
            Objects.equals( variant, that.variant ) && Objects.equals( displayVariant, that.displayVariant ) &&
            Objects.equals( country, that.country ) && Objects.equals( displayCountry, that.displayCountry );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( tag, displayName, language, displayLanguage, variant, displayVariant, country, displayCountry );
    }
}
