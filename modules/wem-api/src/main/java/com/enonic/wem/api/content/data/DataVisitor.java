package com.enonic.wem.api.content.data;

import com.enonic.wem.api.content.data.type.PropertyType;

public abstract class DataVisitor
{
    private PropertyType propertyType;

    public DataVisitor restrictType( PropertyType propertyType )
    {
        this.propertyType = propertyType;
        return this;
    }

    public void traverse( final Iterable<Entry> entryIterable )
    {
        for ( Entry entry : entryIterable )
        {
            if ( entry.isData() )
            {
                final Property property = entry.toData();
                if ( propertyType != null && !propertyType.equals( property.getType() ) )
                {
                    continue;
                }
                visit( property );
            }
            else if ( entry.isDataSet() )
            {
                traverse( entry.toDataSet() );
            }
        }
    }

    public abstract void visit( Property property );
}
