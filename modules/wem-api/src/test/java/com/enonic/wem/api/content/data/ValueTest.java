package com.enonic.wem.api.content.data;


import org.joda.time.DateMidnight;
import org.junit.Test;

import com.enonic.wem.api.content.AbstractEqualsTest;

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
}
