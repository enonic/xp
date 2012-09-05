package com.enonic.wem.core.content.data;


public interface EntrySelector
{
    Data getData( EntryPath path );

    DataSet getDataSet( EntryPath path );
}
