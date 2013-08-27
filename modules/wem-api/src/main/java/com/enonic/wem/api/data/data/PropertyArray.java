package com.enonic.wem.api.data.data;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.data.data.type.ValueType;


public class PropertyArray
    extends DataArray<Property>
{
    private final ValueType type;

    private PropertyArray( final Builder builder )
    {
        super( builder.parent, builder.name );

        Preconditions.checkNotNull( builder.dataType, "dataType cannot be null" );
        this.type = builder.dataType;
    }

    public ValueType getType()
    {
        return type;
    }

    @Override
    void add( final Property property )
    {
        super.add( property );
    }

    public Value getValue( final int index )
    {
        return getData( index ).getValue();
    }

    void checkType( Property property )
    {
        if ( !getType().equals( property.getValueType() ) )
        {
            throw new IllegalArgumentException(
                "Array [" + getPath() + "] expects Property of type [" + getType() + "]. Property [" + property.getPath() +
                    "] was of type: " +
                    property.getValueType() );
        }
    }

    public static Builder newPropertyArray()
    {
        return new Builder();
    }

    public static class Builder
    {
        private ValueType dataType;

        private String name;

        private DataSet parent;

        public Builder propertyType( ValueType value )
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
