package com.enonic.xp.issue;

import java.util.UUID;

public class IssueId
{
    private final UUID id = UUID.randomUUID();

    private IssueId()
    {
    }

    public static IssueId create()
    {
        return new IssueId();
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
