package com.enonic.xp.form.inputtype;


import org.junit.Test;

import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.inputtype.GeoPoint;

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
