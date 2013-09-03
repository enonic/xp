package com.enonic.wem.api.content.data;


import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.RootDataSet;

public final class ContentData
    extends RootDataSet
{
    public ContentData()
    {
        // default
    }

    public ContentData( final RootDataSet rootDataSet )
    {
        super( DataSet.newDataSet( rootDataSet ) );
    }
}
