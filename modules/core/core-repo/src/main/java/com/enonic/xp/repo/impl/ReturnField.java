package com.enonic.xp.repo.impl;

import com.enonic.xp.index.IndexPath;

class ReturnField
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
