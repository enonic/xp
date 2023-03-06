package com.enonic.xp.node;


import java.util.List;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import com.enonic.xp.support.SerializableUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    void hasTrailingDivider()
    {
        assertTrue( new NodePath( "first/" ).hasTrailingDivider() );
        assertFalse( new NodePath( "first" ).hasTrailingDivider() );
        assertFalse( new NodePath( "/" ).hasTrailingDivider() );
        assertFalse( NodePath.ROOT.hasTrailingDivider() );
    }

    @Test
    void trimTrailingDivider()
    {
        assertEquals( "first", new NodePath( "first/" ).trimTrailingDivider().toString() );
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
        assertEquals( "first", new NodePath( "first" ).getName() );
        assertEquals( "first", new NodePath( "/first" ).getName() );
        assertEquals( "first", new NodePath( "first/" ).getName() );
        assertEquals( "second", new NodePath( "first/second" ).getName() );
        assertEquals( "second", new NodePath( "/first/second" ).getName() );
        assertEquals( "second", new NodePath( "/first/second/" ).getName() );
        assertEquals( "second", new NodePath( "first/second/" ).getName() );
    }

    @Test
    void ROOT()
    {
        assertEquals( "/", NodePath.ROOT.toString() );
        assertTrue( NodePath.ROOT.isAbsolute() );
        assertFalse( NodePath.ROOT.isRelative() );
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
    void removeFromBeginning()
    {
        assertEquals( "/three/four", new NodePath( "/one/two/three/four" ).removeFromBeginning( new NodePath( "/one/two" ) ).toString() );
        assertEquals( "/three", new NodePath( "/one/two/three" ).removeFromBeginning( new NodePath( "/one/two" ) ).toString() );
        assertEquals( "/", new NodePath( "/one/two" ).removeFromBeginning( new NodePath( "/one/two" ) ).toString() );
        assertEquals( "/", new NodePath( "/one" ).removeFromBeginning( new NodePath( "/one" ) ).toString() );
        assertEquals( "/", new NodePath( "/one" ).removeFromBeginning( new NodePath( "/one" ) ).toString() );
        assertThrows( IllegalStateException.class, () -> new NodePath( "/" ).removeFromBeginning( new NodePath( "/one" ) ) );
    }

    @Test
    void removeFirstElement()
    {
        assertEquals( "/path", NodePath.create(new NodePath( "/content/path" )).removeFirstElement().build().toString() );
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
    void getElementAsString()
    {
        assertEquals( "one", new NodePath( "/one/two/three/four" ).getElementAsString( 0 ) );
        assertEquals( "three", new NodePath( "/one/two/three/four" ).getElementAsString( 2 ) );
        assertEquals( "four", new NodePath( "/one/two/three/four/" ).getElementAsString( 3 ) );
        assertEquals( "two", new NodePath( "one/two" ).getElementAsString( 1 ) );
        assertEquals( "two", new NodePath( "one/two/" ).getElementAsString( 1 ) );
    }

    @Test
    void elementCount()
    {
        assertEquals( 4, new NodePath( "/one/two/three/four" ).elementCount() );
        assertEquals( 4, new NodePath( "/one/two/three/four" ).elementCount() );
        assertEquals( 4, new NodePath( "/one/two/three/four/" ).elementCount() );
        assertEquals( 2, new NodePath( "one/two" ).elementCount() );
        assertEquals( 2, new NodePath( "one/two/" ).elementCount() );
        assertEquals( 0, new NodePath( "/" ).elementCount() );
    }

    @Test
    void asRelative()
    {
        assertEquals( new NodePath( "one/two/three/four" ), new NodePath( "/one/two/three/four" ).asRelative() );
        assertEquals( new NodePath( "one/two/three/four" ), new NodePath( "one/two/three/four" ).asRelative() );
        assertEquals( new NodePath( "" ), new NodePath( "" ).asRelative() );
        assertEquals( new NodePath( "" ), new NodePath( "/" ).asRelative() );
    }

    @Test
    void asAbsolute()
    {
        assertEquals( new NodePath( "/one/two/three/four" ), new NodePath( "one/two/three/four" ).asAbsolute() );
        assertEquals( new NodePath( "/one/two/three/four" ), new NodePath( "/one/two/three/four" ).asAbsolute() );
        assertEquals( new NodePath( "/" ), new NodePath( "" ).asAbsolute() );
        assertEquals( new NodePath( "/" ), new NodePath( "/" ).asAbsolute() );
    }

    @Test
    void equalsContract()
    {
        EqualsVerifier.forClass( NodePath.class ).withNonnullFields( "path" ).verify();
    }

    @Test
    public void serializable()
    {
        final NodePath nodePath = new NodePath( "/abc" );
        final byte[] serializedObject = SerializableUtils.serialize( nodePath );
        final NodePath deserializedObject = (NodePath) SerializableUtils.deserialize( serializedObject );
        assertEquals( nodePath, deserializedObject );
    }

    @Test
    public void nodeWithParent()
    {
        assertEquals( "/r/abc", new NodePath( new NodePath( "/r" ), NodeName.from( "abc" ) ).toString() );
        assertEquals( "/r/abc", new NodePath( new NodePath( "/r/" ), NodeName.from( "abc" ) ).toString() );
        assertEquals( "/abc", new NodePath( NodePath.ROOT, NodeName.from( "abc" ) ).toString() );
    }
}
