package com.enonic.wem.api.content.data;

import java.util.ArrayList;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.data.type.BasePropertyType;


public class PropertyArray
    extends EntryArray
{
    private final BasePropertyType type;

    private final ArrayList<Value> valueList = new ArrayList<Value>();

    private PropertyArray( final Builder builder )
    {
        super( builder.parent, builder.name );

        Preconditions.checkNotNull( builder.dataType, "dataType cannot be null" );
        this.type = builder.dataType;
    }

    public BasePropertyType getType()
    {
        return type;
    }

    @Override
    void add( final Entry entry )
    {
        super.add( entry );
        valueList.add( entry.toData().getValue() );
    }

    @Override
    void set( final int index, final Entry entry )
    {
        super.set( index, entry );
        if ( overwritesExisting( index, valueList ) )
        {
            valueList.set( index, entry.toData().getValue() );
        }
        else
        {
            valueList.add( entry.toData().getValue() );
        }
    }

    public Value getValue( final int index )
    {
        return valueList.get( index );
    }

    void checkType( Entry entry )
    {
        if ( !( entry instanceof Property ) )
        {
            throw new IllegalArgumentException(
                "Unexpected type of entry for Data array at path [" + getPath() + "]: " + entry.getClass().getSimpleName() );
        }
        final Property property = (Property) entry;
        if ( !getType().equals( property.getType() ) )
        {
            throw new IllegalArgumentException(
                "Array [" + getPath() + "] expects Data of type [" + getType() + "]. Data [" + entry.getPath() + "] was of type: " +
                    property.getType() );
        }
    }

    public static Builder newPropertyArray()
    {
        return new Builder();
    }

    public static class Builder
    {
        private BasePropertyType dataType;

        private String name;

        private DataSet parent;

        public Builder dataType( BasePropertyType value )
        {
            this.dataType = value;
            return this;
        }

        public Builder name( String value )
        {
            this.name = value;
            return this;
        }

        public Builder parent( DataSet value )
        {
            this.parent = value;
            return this;
        }

        public PropertyArray build()
        {
            return new PropertyArray( this );
        }
    }
}
