package com.enonic.wem.api.content.data;


import org.joda.time.DateMidnight;
import org.junit.Test;

import com.enonic.wem.api.content.AbstractEqualsTest;
import com.enonic.wem.api.content.data.type.DataTypes;

import static junit.framework.Assert.assertEquals;
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
                return Value.newValue().type( DataTypes.TEXT ).value( "aaa" ).build();
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{Value.newValue().type( DataTypes.TEXT ).value( "bbb" ).build(),
                    Value.newValue().type( DataTypes.HTML_PART ).value( "aaa" ).build(),};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return Value.newValue().type( DataTypes.TEXT ).value( "aaa" ).build();
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return Value.newValue().type( DataTypes.TEXT ).value( "aaa" ).build();
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }

    @Test
    public void isJavaType()
    {
        assertTrue( Value.newValue().type( DataTypes.TEXT ).value( "Some text" ).build().isJavaType( String.class ) );
        assertTrue( Value.newValue().type( DataTypes.DATE_MIDNIGHT ).value( DateMidnight.now() ).build().isJavaType( DateMidnight.class ) );
    }

    @Test
    public void build_throws_exception_when_value_is_not_of_expected_type()
    {
        try
        {

            Value.newValue().type( DataTypes.TEXT ).value( DateMidnight.now() ).build();
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            assertEquals( "Object expected to be of type [String]: DateMidnight", e.getMessage() );
        }

    }
}
