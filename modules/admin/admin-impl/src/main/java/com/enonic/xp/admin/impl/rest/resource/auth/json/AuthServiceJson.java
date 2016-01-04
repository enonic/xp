package com.enonic.xp.admin.impl.rest.resource.auth.json;

public class AuthServiceJson
{
    private String key;

    private String displayName;

    public AuthServiceJson( final String key, final String displayName )
    {
        this.key = key;
        this.displayName = displayName;
    }

    public String getKey()
    {
        return key;
    }

    public String getDisplayName()
    {
        return displayName;
    }
}
