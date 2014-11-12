package com.enonic.wem.core.entity;


import com.enonic.wem.repo.NodeId;

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
