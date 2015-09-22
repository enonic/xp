package com.enonic.wem.repo.internal.storage;

import com.enonic.xp.index.IndexPath;

public class ReturnField
{
    private final IndexPath indexPath;

    public ReturnField( final IndexPath indexPath )
    {
        this.indexPath = indexPath;
    }

    public String getPath()
    {
        return indexPath.getPath();
    }
}
