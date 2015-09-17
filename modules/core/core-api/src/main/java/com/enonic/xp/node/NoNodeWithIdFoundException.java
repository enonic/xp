package com.enonic.xp.node;

import com.google.common.annotations.Beta;

@Beta
public class NoNodeWithIdFoundException
    extends NoNodeFoundException
{
    private final NodeId id;

    public NoNodeWithIdFoundException( final NodeId id )
    {
        super( "No item with id " + id + " found" );
        this.id = id;
    }

    public NodeId getId()
    {
        return id;
    }
}
