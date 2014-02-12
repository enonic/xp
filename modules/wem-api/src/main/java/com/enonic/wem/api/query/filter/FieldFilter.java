package com.enonic.wem.api.query.filter;

public abstract class FieldFilter
    extends Filter
{
    private final String fieldName;

    protected FieldFilter( final String fieldName )
    {
        this.fieldName = fieldName;
    }

    public String getFieldName()
    {
        return fieldName;
    }


}
