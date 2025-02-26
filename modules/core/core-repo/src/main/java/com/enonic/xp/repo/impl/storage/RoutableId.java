package com.enonic.xp.repo.impl.storage;

import java.util.Objects;

public final class RoutableId
{
    public final String id;

    public final String routing;

    public RoutableId( final String id )
    {
        this.id = id;
        this.routing = null;
    }

    public RoutableId( final String id, final String routing )
    {
        this.id = id;
        this.routing = routing;
    }

    @Override
    public boolean equals( final Object o )
    {
        return ( o instanceof final RoutableId that ) && Objects.equals( id, that.id ) && Objects.equals( routing, that.routing );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( id, routing );
    }
}
