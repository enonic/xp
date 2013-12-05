package com.enonic.wem.query.filter;

public abstract class Filter
{
    private final String fieldName;

    protected Filter( final String fieldName )
    {
        this.fieldName = fieldName;
    }

    public String getFieldName()
    {
        return fieldName;
    }


    public static ContentTypeFilter.Builder newContentTypeFilter()
    {
        return new ContentTypeFilter.Builder();
    }

    public static GenericValueFilter.Builder newValueQueryFilter()
    {
        return new GenericValueFilter.Builder();
    }

    public static ExistsFilter newExistsFilter( final String fieldName )
    {
        return new ExistsFilter( fieldName );
    }

}

