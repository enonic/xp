package com.enonic.xp.name;


import org.junit.Test;

import com.enonic.xp.support.AbstractEqualsTest;

import static org.junit.Assert.*;

public class NameTest
{
    @Test
    public void equals()
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
    public void valid()
        throws Exception
    {
        create( "test" );
    }

    @Test
    public void valid_norwegian_chars()
        throws Exception
    {
        create( "test åæø" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void linebreak()
        throws Exception
    {
        create( "Hepp\nHapp" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void tab()
        throws Exception
    {
        create( "Hepp\tHapp" );
    }

    @Test
    public void chinese()
        throws Exception
    {
        final String chineseName = "\u306d\u304e\u30de\u30e8\u713c\u304d";

        create( chineseName );
    }

    @Test
    public void testCyrillic()
    {
        create( "Норвегия" );
    }

    @Test
    public void controlCharacters()
        throws Exception
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
    public void valid_special_characters()
        throws Exception
    {
        create( "test åæø" );
    }

    @Test
    public void colon_is_valid()
        throws Exception
    {
        create( "test:tast" );
    }

    @Test
    public void additional_allowed_is_valid()
        throws Exception
    {
        create( "^:;#$%&()," );
    }


    @Test(expected = IllegalArgumentException.class)
    public void slash_not_valid()
        throws Exception
    {
        create( "test/me" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void backslash_not_valid()
        throws Exception
    {
        create( "test\\me" );
    }

    private Name create( final String name )
    {
        return new Name( name );
    }
}
