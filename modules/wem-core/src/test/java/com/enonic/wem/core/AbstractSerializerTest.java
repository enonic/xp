package com.enonic.wem.core;


import org.codehaus.jackson.JsonNode;
import org.elasticsearch.common.joda.time.DateTimeUtils;
import org.junit.After;

public abstract class AbstractSerializerTest
{
    private final TestUtil testUtil;

    protected AbstractSerializerTest()
    {
        testUtil = new TestUtil( this );
    }

    @After
    public final void afterAbstractSerializerTest()
    {
        DateTimeUtils.setCurrentMillisSystem();
    }

    protected String jsonToString( final JsonNode node )
    {
        return testUtil.jsonToString( node );
    }

    protected String getXmlAsString( final String fileName )
    {
        return testUtil.getXmlFileAsString( fileName );
    }

    protected String getJsonAsString( final String fileName )
    {
        return testUtil.getJsonFileAsString( fileName );
    }
}
