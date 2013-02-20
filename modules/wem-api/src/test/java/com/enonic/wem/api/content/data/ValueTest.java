package com.enonic.wem.api.content.data;


import org.joda.time.DateMidnight;
import org.junit.Test;

import com.enonic.wem.api.content.data.type.DataTypes;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class ValueTest
{
    @Test
    public void isJavaType()
    {
        assertTrue( Value.newValue().type( DataTypes.TEXT ).value( "Some text" ).build().isJavaType( String.class ) );
        assertTrue( Value.newValue().type( DataTypes.DATE ).value( DateMidnight.now() ).build().isJavaType( DateMidnight.class ) );
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
