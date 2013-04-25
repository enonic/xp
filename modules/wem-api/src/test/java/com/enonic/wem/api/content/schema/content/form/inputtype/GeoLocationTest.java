package com.enonic.wem.api.content.schema.content.form.inputtype;


import org.junit.Test;

import com.enonic.wem.api.content.data.Value;
import com.enonic.wem.api.content.data.type.PropertyTypes;

import static junit.framework.Assert.assertEquals;

public class GeoLocationTest
{
    @Test
    public void newValue()
    {
        Value value = new GeoLocation().newValue( "59.913869,10.752245" );
        assertEquals( "59.913869,10.752245", value.getString() );
        assertEquals( PropertyTypes.GEOGRAPHIC_COORDINATE, value.getType() );
    }
}
