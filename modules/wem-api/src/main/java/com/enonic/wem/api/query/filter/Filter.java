package com.enonic.wem.api.query.filter;

public abstract class Filter
{
    public static ContentTypeFilter.Builder newContentTypeFilter()
    {
        return new ContentTypeFilter.Builder();
    }

    public static GenericValueFilter.Builder newValueQueryFilter()
    {
        return new GenericValueFilter.Builder();
    }

    public static BooleanFilter.Builder newBooleanFilter()
    {
        return new BooleanFilter.Builder();
    }

}

