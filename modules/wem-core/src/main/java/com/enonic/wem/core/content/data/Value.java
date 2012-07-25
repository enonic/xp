package com.enonic.wem.core.content.data;

import com.enonic.wem.core.content.type.configitem.Field;


public class Value
    extends Entry
{
    private EntryPath valuePath;

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
    public EntryPath getPath()
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

        public Builder path( EntryPath value )
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
