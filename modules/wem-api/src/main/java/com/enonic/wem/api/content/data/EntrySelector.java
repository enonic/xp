package com.enonic.wem.api.content.data;


public interface EntrySelector
{
    Data getData( EntryPath path );

    DataSet getDataSet( EntryPath path );
}
