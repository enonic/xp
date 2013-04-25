package com.enonic.wem.api.content.data;

import com.enonic.wem.api.content.data.type.ValueType;

public abstract class PropertyVisitor
{
    private ValueType valueType;

    public PropertyVisitor restrictType( ValueType valueType )
    {
        this.valueType = valueType;
        return this;
    }

    public void traverse( final Iterable<Entry> entryIterable )
    {
        for ( Entry entry : entryIterable )
        {
            if ( entry.isProperty() )
            {
                final Property property = entry.toProperty();
                if ( valueType != null && !valueType.equals( property.getType() ) )
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
