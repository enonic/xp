package com.enonic.wem.api.query.filter;

public class ExistsFilter
    extends FieldFilter
{
    public static ExistsFilter newExistsFilter( final String fieldName )
    {
        return new ExistsFilter( fieldName );
    }

    public ExistsFilter( final String fieldName )
    {
        super( fieldName );
    }
}
