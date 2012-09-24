package com.enonic.wem.api.content.datatype;


import org.joda.time.DateMidnight;
import org.junit.Test;

import static org.junit.Assert.*;

public class JavaTypeTest
{
    @Test
    public void resolveType()
    {
        assertEquals( JavaType.STRING, JavaType.resolveType( "String" ) );
        assertEquals( JavaType.DATE, JavaType.resolveType( new DateMidnight( 2012, 1, 1 ) ) );
        assertFalse( JavaType.STRING.equals( JavaType.resolveType( Boolean.FALSE ) ) );
        assertNull( JavaType.resolveType( new java.util.Date() ) );
    }


    @Test
    public void valid()
    {
        long longValue = 2L;
        assertFalse( JavaType.STRING.isInstance( longValue ) );
        assertFalse( JavaType.DATE.isInstance( longValue ) );
        assertTrue( JavaType.LONG.isInstance( longValue ) );
        assertFalse( JavaType.DOUBLE.isInstance( longValue ) );

        DateMidnight dateValue = new DateMidnight( 2012, 8, 31 );
        assertFalse( JavaType.STRING.isInstance( dateValue ) );
        assertTrue( JavaType.DATE.isInstance( dateValue ) );
        assertFalse( JavaType.LONG.isInstance( dateValue ) );
        assertFalse( JavaType.DOUBLE.isInstance( dateValue ) );
    }
}
