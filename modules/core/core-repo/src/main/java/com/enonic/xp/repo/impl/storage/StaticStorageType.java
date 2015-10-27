package com.enonic.xp.repo.impl.storage;

import com.enonic.xp.repo.impl.StorageType;

public enum StaticStorageType
    implements StorageType
{
    BRANCH, VERSION;

    @Override
    public String getName()
    {
        return this.name().toLowerCase();
    }
}
