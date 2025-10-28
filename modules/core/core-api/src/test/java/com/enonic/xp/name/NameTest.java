package com.enonic.xp.name;


import org.junit.jupiter.api.Test;

import com.enonic.xp.support.AbstractEqualsTest;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

class NameTest
{
    @Test
    void equals()
    {
        AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                return create( "name" );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{create( "other" )};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return create( "name" );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return create( "name" );
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }

    @Test
    void valid()
    {
        create( "test" );
    }

    @Test
    void valid_norwegian_chars()
    {
        create( "test åæø" );
    }

    @Test
    void valid_spanish_chars()
    {
        create( "test ÑñçÇàèìòùáéíóú" );
    }

    @Test
    void linebreak()
    {
        assertThrows(IllegalArgumentException.class, () -> create( "Hepp\nHapp" ));
    }

    @Test
    void tab()
    {
        assertThrows(IllegalArgumentException.class, () -> create( "Hepp\tHapp" ));
    }

    @Test
    void chinese()
    {
        final String chineseName = "\u306d\u304e\u30de\u30e8\u713c\u304d";

        create( chineseName );
    }

    @Test
    void testCyrillic()
    {
        create( "Норвегия" );
    }

    @Test
    void controlCharacters()
    {
        char[] control =
            {'\u0000', '\u0001', '\u0002', '\u0003', '\u0004', '\u0005', '\u0006', '\u0007', '\u0008', '\u0009', '\u000b', '\u000c',
                '\u000e', '\u000f', '\u0010', '\u0011', '\u0012', '\u0012', '\u0013', '\u0014', '\u0015', '\u0016', '\u0017', '\u0018',
                '\u0019', '\u001A', '\u001B', '\u001C', '\u001D', '\u001E', '\u001F', '\u007F', '\u0080', '\u0081', '\u0082', '\u0083',
                '\u0084', '\u0085', '\u0086', '\u0087', '\u0088', '\u0089', '\u008a', '\u008b', '\u008c', '\u008d', '\u008e', '\u008f',
                '\u0090', '\u0091', '\u0092', '\u0093', '\u0094', '\u0095', '\u0096', '\u0097', '\u0098', '\u0099', '\u009A', '\u009B',
                '\u009C', '\u009D', '\u009E', '\u009F'};

        for ( final char c : control )
        {
            try
            {
                create( c + "" );
                fail( "expected to throw illegal argument for unicode character: " + NameCharacterHelper.getUnicodeString( c ) );
            }
            catch ( Exception e )
            {
                // Expected
            }
        }
    }

    @Test
    void valid_special_characters()
    {
        create( "test åæø" );
    }

    @Test
    void colon_is_valid()
    {
        create( "test:tast" );
    }

    @Test
    void additional_allowed_is_valid()
    {
        create( "^:;#$%&(),@[]!{}=_.-" );
    }

    @Test
    void slash_not_valid()
    {
        assertThrows(IllegalArgumentException.class, () -> create( "test/me" ));
    }

    @Test
    void backslash_not_valid()
    {
        assertThrows(IllegalArgumentException.class, () -> create( "test\\me" ));
    }

    private Name create( final String name )
    {
        return new Name( name )
        {
        };
    }
}
