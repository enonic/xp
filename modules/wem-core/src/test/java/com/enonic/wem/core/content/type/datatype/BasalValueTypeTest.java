package com.enonic.wem.core.content.type.datatype;


import org.joda.time.DateMidnight;
import org.junit.Test;

import static org.junit.Assert.*;

public class BasalValueTypeTest
{
    @Test
    public void resolveType()
    {
        assertEquals( BasalValueType.STRING, BasalValueType.resolveType( "String" ) );
        assertEquals( BasalValueType.DATE, BasalValueType.resolveType( new DateMidnight( 2012, 1, 1 ) ) );
        assertFalse( BasalValueType.STRING.equals( BasalValueType.resolveType( Boolean.FALSE ) ) );
        assertNull( BasalValueType.resolveType( new java.util.Date() ) );
    }
}
