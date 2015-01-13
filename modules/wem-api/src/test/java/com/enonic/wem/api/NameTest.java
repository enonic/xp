package com.enonic.wem.api;


import org.junit.Test;

import com.enonic.wem.api.support.AbstractEqualsTest;

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
                return new Name( "name" );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{new Name( "other" )};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return new Name( "name" );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return new Name( "name" );
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }

    @Test
    public void valid()
        throws Exception
    {
        Name.from( "test" );
    }

    @Test
    public void valid_norwegian_chars()
        throws Exception
    {
        Name.from( "test åæø" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void linebreak()
        throws Exception
    {
        Name.from( "Hepp\nHapp" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void tab()
        throws Exception
    {
        Name.from( "Hepp\tHapp" );
    }

    @Test
    public void chinese()
        throws Exception
    {
        final String chineseName = "\u306d\u304e\u30de\u30e8\u713c\u304d";

        Name.from( chineseName );
    }

    @Test
    public void testCyrillic()
    {
        Name.from( "Норвегия" );
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
                Name.from( c + "" );
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
        Name.from( "test åæø" );
    }

    @Test
    public void colon_is_valid()
        throws Exception
    {
        Name.from( "test:tast" );
    }

    @Test
    public void additional_allowed_is_valid()
        throws Exception
    {
        Name.from( "^:;#$%&()" );
    }


    @Test(expected = IllegalArgumentException.class)
    public void slash_not_valid()
        throws Exception
    {
        Name.from( "test/me" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void backslash_not_valid()
        throws Exception
    {
        Name.from( "test\\me" );
    }
}
