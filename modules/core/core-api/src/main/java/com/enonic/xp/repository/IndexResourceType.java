package com.enonic.xp.repository;

public enum IndexResourceType
{
    MAPPING,
    SETTINGS;

    public String getName()
    {
        return this.name().toLowerCase();
    }

}
