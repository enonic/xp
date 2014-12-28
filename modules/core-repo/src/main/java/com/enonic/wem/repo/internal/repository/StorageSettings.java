package com.enonic.wem.repo.internal.repository;

import com.google.common.collect.ImmutableMap;

public class StorageSettings
{
    private final ImmutableMap<String, String> settings;

    public StorageSettings( final ImmutableMap<String, String> settings )
    {
        this.settings = settings;
    }


}
