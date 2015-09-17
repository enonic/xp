package com.enonic.wem.repo.internal.storage;

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
