package com.enonic.wem.api.query;

class FieldImpl
    implements Field
{
    private final String fieldName;

    public FieldImpl( final String fieldName )
    {
        this.fieldName = fieldName;
    }

    @Override
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
