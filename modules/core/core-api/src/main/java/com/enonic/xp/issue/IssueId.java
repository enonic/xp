package com.enonic.xp.issue;

import java.util.UUID;

public final class IssueId
{
    private final UUID id;

    private IssueId()
    {
        id = UUID.randomUUID();
    }

    private IssueId( UUID id )
    {
        this.id = id;
    }

    public static IssueId create()
    {
        return new IssueId();
    }

    public static IssueId from( String uuid )
    {
        return new IssueId( UUID.fromString( uuid ) );
    }

    @Override
    public boolean equals( final Object o )
    {
        return ( o instanceof IssueId ) && ( (IssueId) o ).id.equals( this.id );
    }

    @Override
    public int hashCode()
    {
        return this.id.hashCode();
    }

    @Override
    public String toString()
    {
        return this.id.toString();
    }
}
