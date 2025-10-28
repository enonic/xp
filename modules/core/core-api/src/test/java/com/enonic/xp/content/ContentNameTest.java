package com.enonic.xp.content;


import org.junit.jupiter.api.Test;

import com.enonic.xp.support.AbstractEqualsTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ContentNameTest
{
    @Test
    void equals()
    {
        AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                return ContentName.from( "mycontent" );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{ContentName.from( "myothercontent" )};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return ContentName.from( "mycontent" );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return ContentName.from( "mycontent" );
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }

    @Test
    void testName()
    {
        final ContentName name = ContentName.from( "mycontent" );
        assertEquals( false, name.isUnnamed() );
        assertEquals( false, name.hasUniqueness() );
        assertEquals( "mycontent", name.toString() );
    }

    @Test
    void testUnnamed()
    {
        final ContentName name = ContentName.from( "__unnamed__" );
        assertEquals( true, name.isUnnamed() );
        assertEquals( false, name.hasUniqueness() );
        assertEquals( "__unnamed__", name.toString() );
    }

    @Test
    void testUnnamedWithUniqueness()
    {
        final ContentName name = ContentName.from( "__unnamed__123" );
        assertEquals( true, name.isUnnamed() );
        assertEquals( true, name.hasUniqueness() );
        assertEquals( "__unnamed__123", name.toString() );
    }

    @Test
    void testNewUniqueUnnamed()
    {
        final ContentName name = ContentName.uniqueUnnamed();
        assertEquals( true, name.isUnnamed() );
        assertEquals( true, name.hasUniqueness() );
        assertEquals( true, name.toString().startsWith( "__unnamed__" ) );
    }
}

