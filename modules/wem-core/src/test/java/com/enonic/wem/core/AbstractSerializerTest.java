package com.enonic.wem.core;


import org.elasticsearch.common.joda.time.DateTimeUtils;
import org.junit.After;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.wem.api.support.SerializingTestHelper;

public abstract class AbstractSerializerTest
{
    private final SerializingTestHelper serializingTestHelper;

    protected AbstractSerializerTest()
    {
        serializingTestHelper = new SerializingTestHelper( this, true );
    }

    @After
    public final void afterAbstractSerializerTest()
    {
        DateTimeUtils.setCurrentMillisSystem();
    }

    protected String jsonToString( final JsonNode node )
    {
        return serializingTestHelper.jsonToString( node );
    }

    protected String loadTestXml( final String fileName )
    {
        return serializingTestHelper.loadTextXml( fileName );
    }

    protected String loadJsonAsString( final String fileName )
    {
        return serializingTestHelper.loadJsonAsString( fileName );
    }
}
