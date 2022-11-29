package com.enonic.xp.repo.impl.storage;

import java.util.Locale;

import com.enonic.xp.repo.impl.StorageType;

public enum StaticStorageType
    implements StorageType
{
    BRANCH, VERSION, COMMIT;

    private final String name;

    StaticStorageType()
    {
        this.name = this.name().toLowerCase( Locale.ROOT );
    }

    @Override
    public String getName()
    {
        return this.name;
    }
}
