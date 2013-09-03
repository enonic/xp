package com.enonic.wem.api.query;

public class Field
    implements StaticOperand
{
    private final String fieldName;

    public Field( final String fieldName )
    {
        this.fieldName = fieldName;
    }

    public String getFieldName()
    {
        return this.fieldName;
    }

    @Override
    public String toString()
    {
        return this.fieldName;
    }
}
