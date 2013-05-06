package com.enonic.wem.api.content.data;


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
    DataId getDataId()
    {
        return null;
    }

    @Override
    void setParent( final DataSet entries )
    {
        throw new UnsupportedOperationException( "A ContentData cannot have a parent" );
    }

    @Override
    public int getArrayIndex()
    {
        return -1;
    }
}
