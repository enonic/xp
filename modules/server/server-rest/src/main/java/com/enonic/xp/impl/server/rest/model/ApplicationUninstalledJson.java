package com.enonic.xp.impl.server.rest.model;

public class ApplicationUninstalledJson
{

    private final String key;

    public ApplicationUninstalledJson( final String key )
    {
        this.key = key;
    }

    public String getKey()
    {
        return key;
    }

}
