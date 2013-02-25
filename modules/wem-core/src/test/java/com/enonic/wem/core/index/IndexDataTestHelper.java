package com.enonic.wem.core.index;

import java.util.Map;

import org.elasticsearch.common.collect.Tuple;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentHelper;
import org.elasticsearch.common.xcontent.XContentType;

import static org.junit.Assert.*;

public abstract class IndexDataTestHelper
{

    public static Map<String, Object> getXContentAsMap( XContentBuilder xContentBuilder )
    {
        final Tuple<XContentType, Map<String, Object>> xContentTypeMapTuple = XContentHelper.convertToMap( xContentBuilder.bytes(), false );

        return xContentTypeMapTuple.v2();
    }

    protected void assertContains( final Map<String, Object> xContentAsMap, final String key, final Object value )
    {
        assertTrue( "missing key: " + key, xContentAsMap.containsKey( key ) );
        assertEquals( "wrong value for key: " + key, value, xContentAsMap.get( key ) );
    }

    protected void printXContent( XContentBuilder xContentBuilder )
    {
        final Map<String, Object> xContentAsMap = IndexDataTestHelper.getXContentAsMap( xContentBuilder );

        for ( String key : xContentAsMap.keySet() )
        {
            System.out.println( key + " : " + xContentAsMap.get( key ) );
        }
    }
}
