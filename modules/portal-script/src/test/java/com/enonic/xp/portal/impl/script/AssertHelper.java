package com.enonic.xp.portal.impl.script;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import junit.framework.Assert;

import com.enonic.xp.portal.impl.script.bean.JsObjectConverter;

public final class AssertHelper
{
    private final ObjectMapper mapper;

    public AssertHelper()
    {
        this.mapper = new ObjectMapper();
        this.mapper.enable( SerializationFeature.INDENT_OUTPUT );
        this.mapper.enable( SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS );
        this.mapper.enable( SerializationFeature.WRITE_NULL_MAP_VALUES );
    }

    public void assertNull( final Object actual )
    {
        Assert.assertNull( actual );
    }

    public void assertEquals( final Object expected, final Object actual )
    {
        Assert.assertEquals( expected, actual );
    }

    public void assertEquals( final String message, final Object expected, final Object actual )
    {
        Assert.assertEquals( message, expected, actual );
    }

    public void assertJson( final Object expected, final Object actual )
        throws Exception
    {
        final Object expectedObj = JsObjectConverter.fromJs( expected );
        final Object actualObj = JsObjectConverter.fromJs( actual );

        final String expectedJson = this.mapper.writeValueAsString( expectedObj );
        final String actualJson = this.mapper.writeValueAsString( actualObj );

        Assert.assertEquals( expectedJson, actualJson );
    }
}
