package com.enonic.xp.node;

import java.util.List;

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
            .hasMessage( "NodeName must be in Unicode NFC form" );
    }

    @Test
    void composedFormAllowed()
    {
        assertThat( NodeName.from( "bl\u00E5b\u00E6r" ) ).hasToString( "bl\u00E5b\u00E6r" );
    }

    @Test
    void lettersWithCanonicalDecompositionAllowed()
    {
        for ( final String name : List.of( "Å", "Ω", "豈", "가" ) )
        {
            assertThat( NodeName.from( name ) ).hasToString( name );
        }
    }

    @Test
    void devanagariAllowed()
    {
        for ( final String name : List.of( "\u0939\u092E\u0947\u0902-\u0906\u092A\u0915\u0940-\u0938\u0939\u092E\u0924\u093F-\u091A\u093E\u0939\u093F\u090F", "\u0915\u0941\u0915\u0940\u091C\u093C", "\u091F\u094D\u0930\u0948\u0915\u093F\u0902\u0917", "\u092A\u0930\u094D\u092F\u093E\u0935\u0930\u0923", "\u0939\u093F\u0928\u094D\u0926\u0940", "\u0928\u092E\u0938\u094D\u0924\u0947" ) )
        {
            assertThat( NodeName.from( name ) ).hasToString( name );
        }
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
