package com.enonic.wem.api.content.data.type;


import org.joda.time.DateMidnight;
import org.junit.Test;

import static org.junit.Assert.*;

public class JavaTypeConvertersTest
{
    @Test
    public void resolveType()
    {
        assertEquals( JavaTypeConverters.STRING_CONVERTER, JavaTypeConverters.resolveConverter( "StringConverter" ) );
        assertEquals( JavaTypeConverters.DATE_MIDNIGHT_CONVERTER, JavaTypeConverters.resolveConverter( new DateMidnight( 2012, 1, 1 ) ) );
        assertFalse( JavaTypeConverters.STRING_CONVERTER.equals( JavaTypeConverters.resolveConverter( Boolean.FALSE ) ) );
        assertNull( JavaTypeConverters.resolveConverter( new java.util.Date() ) );
    }

    @Test
    public void valid()
    {
        long longValue = 2L;
        assertFalse( JavaTypeConverters.STRING_CONVERTER.isInstance( longValue ) );
        assertFalse( JavaTypeConverters.DATE_MIDNIGHT_CONVERTER.isInstance( longValue ) );
        assertTrue( JavaTypeConverters.LONG_CONVERTER.isInstance( longValue ) );
        assertFalse( JavaTypeConverters.DOUBLE_CONVERTER.isInstance( longValue ) );

        DateMidnight dateValue = new DateMidnight( 2012, 8, 31 );
        assertFalse( JavaTypeConverters.STRING_CONVERTER.isInstance( dateValue ) );
        assertTrue( JavaTypeConverters.DATE_MIDNIGHT_CONVERTER.isInstance( dateValue ) );
        assertFalse( JavaTypeConverters.LONG_CONVERTER.isInstance( dateValue ) );
        assertFalse( JavaTypeConverters.DOUBLE_CONVERTER.isInstance( dateValue ) );
    }
}
