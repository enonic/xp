package com.enonic.xp.node;

public interface DeleteNodeListener
{
    void nodesDeleted( int count );

    void totalToDelete( int count );
}
