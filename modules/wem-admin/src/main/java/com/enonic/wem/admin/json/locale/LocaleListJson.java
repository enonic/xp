package com.enonic.wem.admin.json.locale;

import java.util.List;
import java.util.Locale;

import com.google.common.collect.ImmutableList;

public class LocaleListJson
{
    private final ImmutableList<LocaleJson> list;

    public LocaleListJson( final Locale[] locales )
    {
        final ImmutableList.Builder<LocaleJson> builder = ImmutableList.builder();
        for ( final Locale locale : locales )
        {
            builder.add( new LocaleJson( locale ) );
        }

        this.list = builder.build();
    }

    public int getTotal()
    {
        return this.list.size();
    }

    public List<LocaleJson> getLocales()
    {
        return this.list;
    }
}
