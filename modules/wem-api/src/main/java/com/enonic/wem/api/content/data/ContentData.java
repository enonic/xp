package com.enonic.wem.api.content.data;


import com.enonic.wem.api.data.DataId;
import com.enonic.wem.api.data.DataSet;

public final class ContentData
    extends DataSet
{
    public ContentData()
    {
        // default
    }

    @Override
    public boolean isRoot()
    {
        return true;
    }

    @Override
    protected DataId getDataId()
    {
        return null;
    }

    @Override
    protected void setParent( final DataSet entries )
    {
        throw new UnsupportedOperationException( "A ContentData cannot have a parent" );
    }

    @Override
    public int getArrayIndex()
    {
        return -1;
    }
}
