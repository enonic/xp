package com.enonic.xp.repository;

import com.enonic.xp.support.AbstractId;

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
