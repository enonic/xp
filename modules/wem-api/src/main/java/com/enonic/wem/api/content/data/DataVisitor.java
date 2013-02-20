package com.enonic.wem.api.content.data;

import com.enonic.wem.api.content.data.type.DataType;

public abstract class DataVisitor
{
    private DataType dataType;

    public DataVisitor restrictType( DataType dataType )
    {
        this.dataType = dataType;
        return this;
    }

    public void traverse( final Iterable<Entry> entryIterable )
    {
        for ( Entry entry : entryIterable )
        {
            if ( entry.isData() )
            {
                final Data data = entry.toData();
                if ( dataType != null && !dataType.equals( data.getType() ) )
                {
                    continue;
                }
                visit( data );
            }
            else if ( entry.isDataSet() )
            {
                traverse( entry.toDataSet() );
            }
        }
    }

    public abstract void visit( Data data );
}
