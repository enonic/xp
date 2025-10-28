package com.enonic.xp.node;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DeleteNodeParamsTest
{
    @Test
    void ok_id() {
        assertEquals( NodeId.from( "id" ), DeleteNodeParams.create().nodeId( NodeId.from( "id" ) ).build().getNodeId() );
    }

    @Test
    void ok_path() {
        assertEquals( new NodePath( "/some/path" ), DeleteNodeParams.create().nodePath( new NodePath( "/some/path" ) ).build().getNodePath() );
    }

    @Test
    void path_and_id_fail() {
        assertThrows( IllegalArgumentException.class, DeleteNodeParams.create().nodeId( NodeId.from( "id" ) ).nodePath( new NodePath( "/some/path" ) )::build );
    }

    @Test
    void no_path_or_id_fail() {
        assertThrows( IllegalArgumentException.class, DeleteNodeParams.create()::build );
    }
}
