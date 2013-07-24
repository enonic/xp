package com.enonic.wem.admin.rest.resource.util.model;

import java.util.Locale;

import com.enonic.wem.admin.rest.resource.model.Item;

public class LocaleJson
    extends Item
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
