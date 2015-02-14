package com.enonic.xp.node;

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
