package com.enonic.wem.admin.rest.resource.content.json;

import java.util.Locale;

import com.google.common.collect.ImmutableList;


public class LocaleListJson
{
    private ImmutableList<LocaleJson> locales;

    public LocaleListJson( final Locale[] locales )
    {
        final ImmutableList.Builder<LocaleJson> builder = ImmutableList.builder();
        for ( final Locale locale : locales )
        {
            builder.add( new LocaleJson( locale ) );
        }

        this.locales = builder.build();
    }

    public ImmutableList<LocaleJson> getLocales()
    {
        return locales;
    }
}
