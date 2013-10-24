package com.enonic.wem.api.entity;


import java.util.List;

import org.junit.Test;

import com.enonic.wem.api.content.AbstractEqualsTest;

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
}
