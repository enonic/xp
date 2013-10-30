package com.enonic.wem.core.index.elastic.indexsource;

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
        IndexSource.Builder builder = IndexSource.newIndexSource();
        builder.addItem( new IndexSourceItem( "test1_string", "value1" ) );
        builder.addItem( new IndexSourceItem( "test2_double", 2.0 ) );
        builder.addItem( new IndexSourceItem( "test3_long", 3L ) );
        builder.addItem( new IndexSourceItem( "test4_date", new DateTime( 2013, 1, 1, 1, 1, 1 ) ) );
        builder.addItem( new IndexSourceItem( "test5_array", new String[]{"one", "two", "three"} ) );

        final XContentBuilder xContentBuilder = XContentBuilderFactory.create( builder.build() );

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

