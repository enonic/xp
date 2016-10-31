package com.enonic.xp.repository;

public class BranchNotFoundException
    extends RuntimeException
{
    public BranchNotFoundException( final String message )
    {
        super( message );
    }
}
