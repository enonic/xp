package com.enonic.wem.api.repository;

public class RepositoryId
{
    private String id;

    private RepositoryId( final String id )
    {
        this.id = id;
    }

    @Override
    public String toString()
    {
        return this.id;
    }

    public static RepositoryId from( final String value )
    {
        return new RepositoryId( value );
    }

}
