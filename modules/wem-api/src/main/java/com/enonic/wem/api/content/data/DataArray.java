package com.enonic.wem.api.content.data;

import java.util.ArrayList;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.data.datatype.BaseDataType;


public class DataArray
    extends EntryArray
{
    private final BaseDataType type;

    private final ArrayList<Value> valueList = new ArrayList<Value>();

    private DataArray( final Builder builder )
    {
        super( builder.parent, builder.name );

        Preconditions.checkNotNull( builder.dataType, "dataType cannot be null" );
        this.type = builder.dataType;
    }

    public BaseDataType getType()
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
        if ( !( entry instanceof Data ) )
        {
            throw new IllegalArgumentException(
                "Unexpected type of entry for Data array at path [" + getPath() + "]: " + entry.getClass().getSimpleName() );
        }
        final Data data = (Data) entry;
        if ( !getType().equals( data.getType() ) )
        {
            throw new IllegalArgumentException(
                "Array [" + getPath() + "] expects Data of type [" + getType() + "]. Data [" + entry.getPath() + "] was of type: " +
                    data.getType() );
        }
    }

    public static Builder newDataArray()
    {
        return new Builder();
    }

    public static class Builder
    {
        private BaseDataType dataType;

        private String name;

        private DataSet parent;

        public Builder dataType( BaseDataType value )
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

        public DataArray build()
        {
            return new DataArray( this );
        }
    }
}
