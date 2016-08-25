package com.enonic.xp.repo.impl.repository;

public enum IndexResourceType
{
    MAPPING,
    SETTINGS;

    public String getName()
    {
        return this.name().toLowerCase();
    }

}
