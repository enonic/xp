package com.enonic.wem.repo.internal.elasticsearch;

import com.enonic.xp.index.IndexPath;

class ReturnField
{
    private final IndexPath indexPath;

    public ReturnField( final IndexPath indexPath )
    {
        this.indexPath = indexPath;
    }

    public static ReturnField from( final String fieldName )
    {
        return new ReturnField( IndexPath.from( fieldName ) );
    }

    public String getPath()
    {
        return indexPath.getPath();
    }
}
