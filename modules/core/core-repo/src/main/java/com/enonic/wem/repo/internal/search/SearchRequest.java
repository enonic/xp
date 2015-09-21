package com.enonic.wem.repo.internal.search;

import com.enonic.wem.repo.internal.storage.StorageSettings;

public class SearchRequest
{
    private StorageSettings settings;

    private int from;

    private int size;


    public int getFrom()
    {
        return from;
    }

    public int getSize()
    {
        return size;
    }

    public StorageSettings getSettings()
    {
        return settings;
    }
}
