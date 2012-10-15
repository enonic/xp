package com.enonic.wem.api.content.data;

public abstract class DataVisitor
{
    public void traverse( final Iterable<Data> dataIterable )
    {
        for ( Data data : dataIterable )
        {
            visit( data );
            if ( data.hasDataSetAsValue() )
            {
                traverse( data.getDataSet() );
            }
        }
    }

    public abstract void visit( Data data );
}
