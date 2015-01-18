package com.enonic.wem.api.repository;

import com.enonic.wem.api.support.AbstractId;

public class RepositoryId
    extends AbstractId
{
    protected RepositoryId( final String id )
    {
        super( id );
    }

    public static RepositoryId from( final String value )
    {
        return new RepositoryId( value );
    }

}
