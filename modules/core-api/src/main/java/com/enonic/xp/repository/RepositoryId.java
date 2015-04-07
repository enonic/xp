package com.enonic.xp.repository;

import com.google.common.annotations.Beta;

import com.enonic.xp.support.AbstractId;

@Beta
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
