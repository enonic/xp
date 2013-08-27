package com.enonic.wem.api.data.data;


import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.junit.Test;

import com.enonic.wem.api.content.AbstractEqualsTest;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.binary.BinaryId;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;

public class ValueTest
{
    @Test
    public void equals()
    {
        AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                return new Value.Text( "aaa" );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{new Value.Text( "bbb" ), new Value.HtmlPart( "aaa" )};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return new Value.Text( "aaa" );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return new Value.Text( "aaa" );
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }

    @Test
    public void isJavaType()
    {
        assertTrue( new Value.Text( "Some text" ).isJavaType( String.class ) );
        assertTrue( new Value.Date( DateMidnight.now() ).isJavaType( DateMidnight.class ) );
    }

    @Test
    public void construct_ContentId()
    {
        ContentId value = ContentId.from( "abc" );

        assertSame( value, new Value.ContentId( value ).getContentId() );
        assertEquals( value, new Value.ContentId( "abc" ).getContentId() );
    }

    @Test
    public void construct_BinaryId()
    {
        BinaryId value = BinaryId.from( "abc" );

        assertSame( value, new Value.BinaryId( value ).getBinaryId() );
        assertEquals( value, new Value.BinaryId( "abc" ).getBinaryId() );
    }

    @Test
    public void construct_Date()
    {
        DateMidnight value = new DateMidnight( 2013, 1, 1 );

        assertSame( value, new Value.Date( value ).getDate() );
        assertEquals( value, new Value.Date( new DateTime( 2013, 1, 1, 12, 0, 0 ) ).getDate() );
        assertEquals( value, new Value.Date( "2013-1-1" ).getDate() );
    }
}
