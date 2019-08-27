package com.enonic.xp.node;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NodeIdTest
{
    @Test
    public void principal()
        throws Exception
    {
        NodeId.from( "user:system:anonymous" );
    }

    @Test
    public void allowedSpecialCharacters()
        throws Exception
    {
        NodeId.from( "_:." );
    }

    @Test
    public void es_id()
        throws Exception
    {
        NodeId.from( "14d5bcaa-1f9b-4b65-b9d6-d9c045e2b0aa" );
    }

    @Test
    public void slash()
        throws Exception
    {
        assertThrows(IllegalArgumentException.class, () -> NodeId.from( "my/path" ));
    }

}
