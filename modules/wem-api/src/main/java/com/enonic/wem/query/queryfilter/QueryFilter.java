package com.enonic.wem.query.queryfilter;

public abstract class QueryFilter
{
    private final String fieldName;

    protected QueryFilter( final String fieldName )
    {
        this.fieldName = fieldName;
    }

    public String getFieldName()
    {
        return fieldName;
    }


    public static ContentTypeQueryFilter.Builder newContentTypeFilter()
    {
        return new ContentTypeQueryFilter.Builder();
    }

    public static GenericValueQueryFilter.Builder newValueQueryFilter()
    {
        return new GenericValueQueryFilter.Builder();
    }

    public static ExistsQueryFilter newExistsFilter( final String fieldName )
    {
        return new ExistsQueryFilter( fieldName );
    }

}

