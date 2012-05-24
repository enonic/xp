package com.enonic.wem.web.rest.locale;

public final class LocaleModel
{
    private String id;

    private String displayName;

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName( String displayName )
    {
        this.displayName = displayName;
    }

    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }
}
