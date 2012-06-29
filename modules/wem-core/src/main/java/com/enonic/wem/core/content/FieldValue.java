package com.enonic.wem.core.content;

import com.enonic.wem.core.content.config.field.Field;


public class FieldValue
    extends FieldEntry
{
    private Field field;

    private FieldEntryPath fieldEntryPath;

    private Object value;

    private FieldValue()
    {
        // protection
    }

    public Field getField()
    {
        return field;
    }

    @Override
    public FieldEntryPath getPath()
    {
        return fieldEntryPath;
    }

    public Object getValue()
    {
        return value;
    }

    @Override
    public FieldEntryJsonGenerator getJsonGenerator()
    {
        return FieldValueJsonGenerator.DEFAULT;
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
        private FieldValue fieldValue;

        public Builder()
        {
            fieldValue = new FieldValue();
        }

        public Builder field( Field field )
        {
            fieldValue.field = field;
            return this;
        }

        public Builder fieldEntryPath( FieldEntryPath value )
        {
            fieldValue.fieldEntryPath = value;
            return this;
        }

        public Builder value( Object value )
        {
            fieldValue.value = value;
            return this;
        }

        public FieldValue build()
        {
            return fieldValue;
        }
    }
}
