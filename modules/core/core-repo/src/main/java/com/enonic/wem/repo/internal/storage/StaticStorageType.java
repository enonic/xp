package com.enonic.wem.repo.internal.storage;

import com.enonic.wem.repo.internal.StorageType;

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
