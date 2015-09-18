package com.enonic.xp.core.impl.export;

import java.time.Instant;

import org.junit.Test;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;

import static org.junit.Assert.*;

public class ImportNodeFactoryTest
{

    @Test
    public void testName()
        throws Exception
    {

        final Node serializedNode = Node.create().
            data( new PropertyTree() ).
            timestamp( Instant.parse( "2010-01-01T10:00:00Z" ) ).
            build();

        final Node importNode = ImportNodeFactory.create().
            serializedNode( serializedNode ).
            importPath( NodePath.create( "/test" ).build() ).
            processNodeSettings( ProcessNodeSettings.create().build() ).
            build().
            execute();

        assertEquals( serializedNode.getTimestamp(), importNode.getTimestamp() );

    }
}