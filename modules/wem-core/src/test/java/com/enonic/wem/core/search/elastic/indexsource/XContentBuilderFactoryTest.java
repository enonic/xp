package com.enonic.wem.core.search.elastic.indexsource;

import java.util.Map;

import org.elasticsearch.common.collect.Tuple;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentHelper;
import org.elasticsearch.common.xcontent.XContentType;
import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.*;

public class XContentBuilderFactoryTest
{

    @Test
    public void testCreateXContentBuilder()
        throws Exception
    {
        IndexSource indexSource = new IndexSource();
        indexSource.addIndexSourceEntry( new IndexSourceEntry( "test1_string", "value1" ) );
        indexSource.addIndexSourceEntry( new IndexSourceEntry( "test2_double", 2.0 ) );
        indexSource.addIndexSourceEntry( new IndexSourceEntry( "test3_long", 3L ) );
        indexSource.addIndexSourceEntry( new IndexSourceEntry( "test4_date", new DateTime( 2013, 1, 1, 1, 1, 1 ) ) );
        indexSource.addIndexSourceEntry( new IndexSourceEntry( "test5_array", new String[]{"one", "two", "three"} ) );

        final XContentBuilder xContentBuilder = XContentBuilderFactory.create( indexSource );

        final Tuple<XContentType, Map<String, Object>> xContentTypeMapTuple = XContentHelper.convertToMap( xContentBuilder.bytes(), true );
        final Map<String, Object> objectValueMap = xContentTypeMapTuple.v2();

        checkObjectValue( "test1_string", objectValueMap );
        checkObjectValue( "test2_double", objectValueMap );
        checkObjectValue( "test3_long", objectValueMap );
        checkObjectValue( "test4_date", objectValueMap );
        checkObjectValue( "test5_array", objectValueMap );

    }

    private void checkObjectValue( final String key1, final Map<String, Object> objectValueMap )
    {
        assertTrue( objectValueMap.containsKey( key1 ) );
        final Object objectValue = objectValueMap.get( key1 );
        assertNotNull( objectValue );
    }
}

