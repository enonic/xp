package com.enonic.wem.api.node;

import org.junit.Test;

import static org.junit.Assert.*;

public class NodePathTest
{
    @Test
    public void root_as_parent()
        throws Exception
    {
        final NodePath appendedPath = NodePath.newNodePath( RootNode.create().build().path(), "/content" ).build();

        assertEquals( NodePath.newPath( "/content" ).build(), appendedPath );
    }

    public void build_with_root()
    {
        final NodePath path = NodePath.newPath( RootNode.create().build().path() ).
            addElement( "/content" ).
            build();

        assertEquals( NodePath.newPath( "/content" ).build(), path );
    }
}