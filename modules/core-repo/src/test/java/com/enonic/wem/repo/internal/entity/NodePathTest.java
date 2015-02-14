package com.enonic.wem.repo.internal.entity;


import java.util.List;

import org.junit.Test;

import com.enonic.xp.support.AbstractEqualsTest;
import com.enonic.xp.node.NodePath;

import static junit.framework.Assert.assertEquals;

public class NodePathTest
{
    @Test
    public void equals()
    {
        AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                return new NodePath( "/myPath/myItem" );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{new NodePath( "/myPath" )};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return new NodePath( "/myPath/myItem" );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return new NodePath( "/myPath/myItem" );
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }

    @Test
    public void isEmpty()
    {
        assertEquals( false, new NodePath( "first" ).isEmpty() );
        assertEquals( true, new NodePath( "" ).isEmpty() );
    }

    @Test
    public void isRoot()
    {
        assertEquals( true, new NodePath( "/" ).isRoot() );
    }

    @Test
    public void isAbsolute()
    {
        assertEquals( true, new NodePath( "/first" ).isAbsolute() );
        assertEquals( false, new NodePath( "first" ).isAbsolute() );
    }

    @Test
    public void hasTrailingDivider()
    {
        assertEquals( true, new NodePath( "first/" ).hasTrailingDivider() );
        assertEquals( false, new NodePath( "first" ).hasTrailingDivider() );
        assertEquals( false, new NodePath( "/" ).hasTrailingDivider() );
    }

    @Test
    public void trimTrailingDivider()
    {
        assertEquals( "first", new NodePath( "first/" ).trimTrailingDivider().toString() );
    }

    @Test
    public void tostring()
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
    public void ROOT()
    {
        assertEquals( "/", NodePath.ROOT.toString() );
        assertEquals( true, NodePath.ROOT.isAbsolute() );
        assertEquals( false, NodePath.ROOT.isRelative() );
        assertEquals( true, NodePath.ROOT.isEmpty() );
        assertEquals( false, NodePath.ROOT.hasTrailingDivider() );
    }

    @Test
    public void getParentPaths()
    {
        NodePath nodePath = new NodePath( "/one/two/three" );
        List<NodePath> parentPaths = nodePath.getParentPaths();
        assertEquals( 3, parentPaths.size() );
        assertEquals( "/one/two", parentPaths.get( 0 ).toString() );
        assertEquals( "/one", parentPaths.get( 1 ).toString() );
        assertEquals( "/", parentPaths.get( 2 ).toString() );
    }

    @Test
    public void removeFromBeginning()
    {
        assertEquals( "/three/four", new NodePath( "/one/two/three/four" ).removeFromBeginning( new NodePath( "/one/two" ) ).toString() );
        assertEquals( "/three", new NodePath( "/one/two/three" ).removeFromBeginning( new NodePath( "/one/two" ) ).toString() );
        assertEquals( "/", new NodePath( "/one/two" ).removeFromBeginning( new NodePath( "/one/two" ) ).toString() );
        assertEquals( "/", new NodePath( "/one" ).removeFromBeginning( new NodePath( "/one" ) ).toString() );
    }

    @Test(expected = IllegalStateException.class)
    public void removeFromBeginning_throws_IllegalStateException()
    {
        assertEquals( "/", new NodePath( "/" ).removeFromBeginning( new NodePath( "/one" ) ).toString() );
    }

}
