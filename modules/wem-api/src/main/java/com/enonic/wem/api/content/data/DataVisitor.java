package com.enonic.wem.api.content.data;

public abstract class DataVisitor
{
    public void traverse( final Iterable<Entry> entryIterable )
    {
        for ( Entry entry : entryIterable )
        {
            visit( entry );
            if ( entry.isDataSet() )
            {
                traverse( entry.toDataSet() );
            }
        }
    }

    public abstract void visit( Entry entry );
}
