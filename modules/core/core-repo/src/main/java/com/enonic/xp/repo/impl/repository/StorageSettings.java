package com.enonic.xp.repo.impl.repository;

import com.google.common.collect.ImmutableMap;

public class StorageSettings
{
    private final ImmutableMap<String, String> settings;

    public StorageSettings( final ImmutableMap<String, String> settings )
    {
        this.settings = settings;
    }


}
