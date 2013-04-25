package com.enonic.wem.api.content.data;


public final class RootDataSet
    extends DataSet
{
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
        throw new UnsupportedOperationException( "A RootDataSet cannot have a parent" );
    }
}
