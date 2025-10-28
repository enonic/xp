package com.enonic.xp.core.impl.export;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ImportNodeFactoryTest
{

    @Test
    void testName()
    {

        final Node serializedNode = Node.create().
            data( new PropertyTree() ).
            timestamp( Instant.parse( "2010-01-01T10:00:00Z" ) ).
            build();

        final Node importNode = ImportNodeFactory.create().
            serializedNode( serializedNode ).
            importPath( new NodePath( "/test" ) ).
            build().
            execute();

        assertEquals( serializedNode.getTimestamp(), importNode.getTimestamp() );

    }
}
