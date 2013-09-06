package com.enonic.wem.admin.json.locale;

import java.util.Locale;

public class LocaleJson
{
    private final Locale locale;

    public LocaleJson( final Locale locale )
    {
        this.locale = locale;
    }

    public String getId()
    {
        return this.locale.toString();
    }

    public String getDisplayName()
    {
        return this.locale.getDisplayName();
    }

}
