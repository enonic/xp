package com.enonic.wem.repo.internal.storage;

public abstract class AbstractStorageDataEntry<T>
    implements StorageDataEntry<T>
{
    private String key;

    private T value;

    public AbstractStorageDataEntry( final String key, final T value )
    {
        this.key = key;
        this.value = value;
    }

    @Override
    public String getKey()
    {
        return key;
    }

    @Override
    public T getValue()
    {
        return value;
    }
}
