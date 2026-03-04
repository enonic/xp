package com.enonic.xp.repository;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public enum IndexResourceType
{
    MAPPING,
    SETTINGS;

    public String getName()
    {
        return this.name().toLowerCase();
    }

}
