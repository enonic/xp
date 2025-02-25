package com.enonic.xp.repo.impl.storage;

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
}
