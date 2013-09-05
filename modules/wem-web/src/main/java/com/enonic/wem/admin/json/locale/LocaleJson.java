package com.enonic.wem.admin.json.locale;

import java.util.Locale;

import com.enonic.wem.admin.json.ItemJson;

public class LocaleJson
    extends ItemJson
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

    @Override
    public boolean getEditable()
    {
        return false;
    }

    @Override
    public boolean getDeletable()
    {
        return false;
    }
}
