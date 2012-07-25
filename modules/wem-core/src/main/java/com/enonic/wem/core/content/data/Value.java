package com.enonic.wem.core.content.data;

import com.enonic.wem.core.content.type.item.Field;


public class Value
    extends Entry
{
    private ValuePath valuePath;

    /**
     * Optional.
     */
    private Field field;

    private Object value;

    private Value()
    {
        // protection
    }

    public Field getField()
    {
        return field;
    }

    @Override
    public ValuePath getPath()
    {
        return valuePath;
    }

    public Object getValue()
    {
        return value;
    }


    @Override
    public String toString()
    {
        return String.valueOf( value );
    }

    public static Builder newBuilder()
    {
        return new Builder();
    }

    public static class Builder
    {
        private Value value;

        public Builder()
        {
            value = new Value();
        }

        public Builder field( Field field )
        {
            value.field = field;
            return this;
        }

        public Builder path( ValuePath value )
        {
            this.value.valuePath = value;
            return this;
        }

        public Builder value( Object value )
        {
            this.value.value = value;
            return this;
        }

        public Value build()
        {
            return value;
        }
    }
}
