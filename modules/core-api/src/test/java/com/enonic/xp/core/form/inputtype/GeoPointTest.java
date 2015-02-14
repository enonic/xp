package com.enonic.xp.core.form.inputtype;


import org.junit.Test;

import com.enonic.xp.core.data.Value;
import com.enonic.xp.core.data.ValueTypes;

import static junit.framework.Assert.assertEquals;

public class GeoPointTest
{
    @Test
    public void newValue()
    {
        Value value = new GeoPoint().newValue( "59.913869,10.752245" );
        assertEquals( "59.913869,10.752245", value.asString() );
        assertEquals( ValueTypes.GEO_POINT, value.getType() );
    }
}
