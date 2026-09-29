package com.enonic.xp.node;

import org.junit.jupiter.api.Test;

import com.enonic.xp.support.AbstractEqualsTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NodeNameTest
{
    @Test
    void testName()
    {
        assertEquals( "name", NodeName.from( "name" ).toString() );
    }

    @Test
    void testBigName()
    {
        NodeName.from( "Name" );
    }

    @Test
    void testAllowedSymbols()
    {
        assertEquals( "your_name.is-okay", NodeName.from( "your_name.is-okay" ).toString() );
    }

    @Test
    void testAsteriskName()
    {
        assertThrows(IllegalArgumentException.class, () -> NodeName.from( "name*value" ));
    }

    @Test
    void testUnderscoreOnlyNotAllowed()
    {
        assertThrows(IllegalArgumentException.class, () -> NodeName.from( "_" ));
    }

    @Test
    void slashNotAllowed()
    {
        assertThrows(IllegalArgumentException.class, () -> NodeName.from( "some/name" ));
    }

    @Test
    void testNameCouldStartWithUnderscore()
    {
        NodeName.from( "_mystuff" );
    }

    @Test
    void start_with_number()
    {
        NodeName.from( "1myname" );
    }

    @Test
    void emojiAllowed()
    {
        assertThat( NodeName.from( "cat\uD83D\uDE00" ) ).hasToString( "cat\uD83D\uDE00" );
    }

    @Test
    void loneSurrogateNotAllowed()
    {
        assertThatThrownBy( () -> NodeName.from( "cat\uD83D" ) ).isInstanceOf( IllegalArgumentException.class )
            .hasMessage( "NodeName must not contain 'U+D83D'" );
    }

    @Test
    void combiningMarkNotAllowed()
    {
        assertThatThrownBy( () -> NodeName.from( "a\u030A" ) ).isInstanceOf( IllegalArgumentException.class )
            .hasMessage( "NodeName must not contain 'U+030A'" );
    }

    @Test
    void equals()
    {
        final AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                return NodeName.from( "name" );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{NodeName.from( "other" )};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return NodeName.from( "name" );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return NodeName.from( "name" );
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }

    @Test
    void testRoot()
    {
        final NodeName name = NodeName.ROOT;
        assertEquals( true, name.isRoot() );
        assertEquals( "", name.toString() );
    }

    @Test
    void testNonRoot()
    {
        final NodeName name = NodeName.from( "test" );
        assertEquals( false, name.isRoot() );
        assertEquals( "test", name.toString() );
    }
}
