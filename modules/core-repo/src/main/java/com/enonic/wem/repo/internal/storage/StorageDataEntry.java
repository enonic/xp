package com.enonic.wem.repo.internal.storage;

public interface StorageDataEntry<T>
{
    public T getValue();

    public String getKey();

}
