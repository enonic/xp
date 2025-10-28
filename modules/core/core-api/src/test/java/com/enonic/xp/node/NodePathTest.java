package com.enonic.xp.node;


import java.util.List;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import com.enonic.xp.support.SerializableUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NodePathTest
{
    @Test
    void isEmpty()
    {
        assertFalse( new NodePath( "first" ).isEmpty() );
        assertTrue( new NodePath( "" ).isEmpty() );
        assertTrue( new NodePath( "/" ).isEmpty() );
        assertTrue( NodePath.ROOT.isEmpty() );
    }

    @Test
    void isRoot()
    {
        assertTrue( new NodePath( "/" ).isRoot() );
    }

    @Test
    void isAbsolute()
    {
        assertTrue( new NodePath( "/first" ).isAbsolute() );
        assertFalse( new NodePath( "first" ).isAbsolute() );
    }

    @Test
    void tostring()
    {
        assertEquals( "", new NodePath( "" ).toString() );
        assertEquals( "/", new NodePath( "/" ).toString() );
        assertEquals( "first", new NodePath( "first" ).toString() );
        assertEquals( "/first", new NodePath( "/first" ).toString() );
        assertEquals( "first/", new NodePath( "first/" ).toString() );
        assertEquals( "first/second", new NodePath( "first/second" ).toString() );
        assertEquals( "/first/second", new NodePath( "/first/second" ).toString() );
        assertEquals( "/first/second/", new NodePath( "/first/second/" ).toString() );
        assertEquals( "first/second/", new NodePath( "first/second/" ).toString() );
    }

    @Test
    void getName()
    {
        assertNull( new NodePath( "" ).getName() );
        assertNull( new NodePath( "/" ).getName() );
        assertEquals( NodeName.from( "first" ), new NodePath( "first" ).getName() );
        assertEquals( NodeName.from( "first" ), new NodePath( "/first" ).getName() );
        assertEquals( NodeName.from( "first" ), new NodePath( "first/" ).getName() );
        assertEquals( NodeName.from( "second" ), new NodePath( "first/second" ).getName() );
        assertEquals( NodeName.from( "second" ), new NodePath( "/first/second" ).getName() );
        assertEquals( NodeName.from( "second" ), new NodePath( "/first/second/" ).getName() );
        assertEquals( NodeName.from( "second" ), new NodePath( "first/second/" ).getName() );
    }

    @Test
    void ROOT()
    {
        assertEquals( "/", NodePath.ROOT.toString() );
        assertTrue( NodePath.ROOT.isAbsolute() );
        assertTrue( NodePath.ROOT.isEmpty() );
    }

    @Test
    void getParentPath()
    {
        assertEquals( "", new NodePath( "first/" ).getParentPath().toString() );
        assertEquals( "", new NodePath( "" ).getParentPath().toString() );
        assertEquals( "/", new NodePath( "/" ).getParentPath().toString() );
        assertEquals( "", new NodePath( "first" ).getParentPath().toString() );
        assertEquals( "/", new NodePath( "/first" ).getParentPath().toString() );
        assertEquals( "first", new NodePath( "first/second" ).getParentPath().toString() );
        assertEquals( "/first", new NodePath( "/first/second" ).getParentPath().toString() );
        assertEquals( "/first/", new NodePath( "/first/second/" ).getParentPath().toString() );
        assertEquals( "first/", new NodePath( "first/second/" ).getParentPath().toString() );
    }

    @Test
    void getParentPaths()
    {
        NodePath nodePath = new NodePath( "/one/two/three" );
        List<NodePath> parentPaths = nodePath.getParentPaths();
        assertEquals( 3, parentPaths.size() );
        assertEquals( "/one/two", parentPaths.get( 0 ).toString() );
        assertEquals( "/one", parentPaths.get( 1 ).toString() );
        assertEquals( "/", parentPaths.get( 2 ).toString() );

        assertEquals( 0, new NodePath( "" ).getParentPaths().size() );
        assertEquals( 0, NodePath.ROOT.getParentPaths().size() );
    }

    @Test
    void compareTo()
    {
        NodePath a = new NodePath( "/a" );
        NodePath b = new NodePath( "/b" );
        NodePath arel = new NodePath( "a" );
        NodePath brel = new NodePath( "b" );
        NodePath atrail = new NodePath( "/a/" );
        NodePath btrail = new NodePath( "/b/" );

        assertThat( a.compareTo( b ) ).isLessThanOrEqualTo( -1 );
        assertThat( b.compareTo( a ) ).isGreaterThanOrEqualTo( 1 );

        assertThat( arel.compareTo( brel ) ).isLessThanOrEqualTo( -1 );
        assertThat( brel.compareTo( arel ) ).isGreaterThanOrEqualTo( 1 );

        assertThat( atrail.compareTo( btrail ) ).isLessThanOrEqualTo( -1 );
        assertThat( btrail.compareTo( atrail ) ).isGreaterThanOrEqualTo( 1 );
    }

    @Test
    void equalsContract()
    {
        EqualsVerifier.forClass( NodePath.class ).withNonnullFields( "path" ).verify();
    }

    @Test
    void serializable()
    {
        final NodePath nodePath = new NodePath( "/abc" );
        final byte[] serializedObject = SerializableUtils.serialize( nodePath );
        final NodePath deserializedObject = (NodePath) SerializableUtils.deserialize( serializedObject );
        assertEquals( nodePath, deserializedObject );
    }

    @Test
    void nodeWithParent()
    {
        assertEquals( "/r/abc", new NodePath( new NodePath( "/r" ), NodeName.from( "abc" ) ).toString() );
        assertEquals( "/r/abc", new NodePath( new NodePath( "/r/" ), NodeName.from( "abc" ) ).toString() );
        assertEquals( "/abc", new NodePath( NodePath.ROOT, NodeName.from( "abc" ) ).toString() );
    }

    @Test
    void create()
    {
        assertEquals( "/my-node", NodePath.create( NodePath.ROOT ).addElement( "my-node" ).build().toString() );
        assertEquals( "/my-node", NodePath.create( NodePath.ROOT ).addElement( "my-node" ).build().toString() );
    }
}
