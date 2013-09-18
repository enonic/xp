package com.enonic.wem.api.item;


import java.util.List;

import org.junit.Test;

import com.enonic.wem.api.content.AbstractEqualsTest;

import static junit.framework.Assert.assertEquals;

public class ItemPathTest
{
    @Test
    public void equals()
    {
        AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                return new ItemPath( "/myPath/myItem" );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{new ItemPath( "/myPath" )};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return new ItemPath( "/myPath/myItem" );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return new ItemPath( "/myPath/myItem" );
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }

    @Test
    public void getParentPaths()
    {
        ItemPath itemPath = new ItemPath( "/one/two/three" );
        List<ItemPath> parentPaths = itemPath.getParentPaths();
        assertEquals( 3, parentPaths.size() );
        assertEquals( "/one/two", parentPaths.get( 0 ).toString() );
        assertEquals( "/one", parentPaths.get( 1 ).toString() );
        assertEquals( "/", parentPaths.get( 2 ).toString() );
    }
}
