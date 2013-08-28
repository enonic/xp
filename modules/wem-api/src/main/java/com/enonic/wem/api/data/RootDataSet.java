package com.enonic.wem.api.data;


public class RootDataSet
    extends DataSet
{
    public RootDataSet()
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
        throw new UnsupportedOperationException( "A RootDataSet cannot have a parent" );
    }

    @Override
    public int getArrayIndex()
    {
        return -1;
    }
}
