package com.enonic.xp.repo.impl.elasticsearch.xcontent;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.elasticsearch.common.collect.Tuple;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentHelper;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Test;

import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.repo.impl.elasticsearch.IndexConstants;
import com.enonic.xp.repo.impl.elasticsearch.document.StoreDocument;
import com.enonic.xp.repo.impl.elasticsearch.document.StoreDocumentDateItem;
import com.enonic.xp.repo.impl.elasticsearch.document.StoreDocumentGeoPointItem;
import com.enonic.xp.repo.impl.elasticsearch.document.StoreDocumentNumberItem;
import com.enonic.xp.repo.impl.elasticsearch.document.StoreDocumentOrderbyItem;
import com.enonic.xp.repo.impl.elasticsearch.document.StoreDocumentStringItem;
import com.enonic.xp.util.GeoPoint;

import static org.junit.Assert.*;

public class StoreDocumentXContentBuilderFactoryTest
{

    @Test
    public void create_given_indexdocument_analyzer()
        throws Exception
    {
        StoreDocument storeDocument = StoreDocument.create().
            indexName( "testindex" ).
            indexTypeName( "test" ).
            analyzer( "myanalyzer" ).
            build();

        final XContentBuilder xContentBuilder = StoreDocumentXContentBuilderFactory.create( storeDocument );

        final Map<String, Object> objectMap = getObjectMap( xContentBuilder );

        final Object objectValue = objectMap.get( IndexConstants.ANALYZER_VALUE_FIELD );

        assertNotNull( objectValue );
        assertTrue( objectValue instanceof String );
        assertEquals( "myanalyzer", objectValue );
    }

    @Test
    public void multiple_orderby_entries_gives_one()
    {
        StoreDocument storeDocument = StoreDocument.create().
            indexName( "testindex" ).
            indexTypeName( "test" ).
            addEntry( new StoreDocumentStringItem( IndexPath.from( "myField" ), "myValue1" ) ).
            addEntry( new StoreDocumentStringItem( IndexPath.from( "myField" ), "myValue2" ) ).
            addEntry( new StoreDocumentOrderbyItem( IndexPath.from( "myField" ), "myOrderByValue1" ) ).
            addEntry( new StoreDocumentOrderbyItem( IndexPath.from( "myField" ), "myOrderByValue2" ) ).
            build();

        final XContentBuilder xContentBuilder = StoreDocumentXContentBuilderFactory.create( storeDocument );

        final Map<String, Object> objectMap = getObjectMap( xContentBuilder );

        final Object myString = objectMap.get( "myfield" );
        assertTrue( myString instanceof ArrayList );
        assertTrue( ( (ArrayList) myString ).size() == 2 );
        final Object myOrder = objectMap.get( "myfield._orderby" );
        assertTrue( myOrder instanceof String );
    }

    @Test
    public void create_given_indexdocument_arrayvalues()
        throws Exception
    {

        StoreDocument storeDocument = StoreDocument.create().
            indexName( "testindex" ).
            indexTypeName( "test" ).
            addEntry( new StoreDocumentStringItem( IndexPath.from( "myField" ), "myValue1" ) ).
            addEntry( new StoreDocumentStringItem( IndexPath.from( "myField" ), "myValue2" ) ).
            addEntry( new StoreDocumentNumberItem( IndexPath.from( "myNumericField" ), 1.0 ) ).
            addEntry( new StoreDocumentNumberItem( IndexPath.from( "myNumericField" ), 2.0 ) ).
            addEntry( new StoreDocumentDateItem( IndexPath.from( "myDateField" ), Instant.now() ) ).
            addEntry( new StoreDocumentDateItem( IndexPath.from( "myDateField" ), Instant.now() ) ).
            addEntry( new StoreDocumentGeoPointItem( IndexPath.from( "myGeoPoint" ),
                                                     ValueFactory.newGeoPoint( GeoPoint.from( "80,80" ) ).toString() ) ).
            addEntry( new StoreDocumentGeoPointItem( IndexPath.from( "myGeoPoint" ),
                                                     ValueFactory.newGeoPoint( GeoPoint.from( "81,81" ) ).toString() ) ).
            build();

        final XContentBuilder xContentBuilder = StoreDocumentXContentBuilderFactory.create( storeDocument );

        final Map<String, Object> objectMap = getObjectMap( xContentBuilder );

        final Collection<Object> objectValue = getObjectValue( "myfield", objectMap );
        assertEquals( 2, objectValue.size() );

        final Collection<Object> numericObjectValue = getObjectValue( "mynumericfield._number", objectMap );
        assertEquals( 2, numericObjectValue.size() );

        final Collection<Object> dateObjectValue = getObjectValue( "mydatefield._datetime", objectMap );
        assertEquals( 2, dateObjectValue.size() );

        final Collection<Object> geoPointObjectValue = getObjectValue( "mygeopoint._geopoint", objectMap );
        assertEquals( 2, geoPointObjectValue.size() );

    }


    private Map<String, Object> getObjectMap( final XContentBuilder xContentBuilder )
    {
        final Tuple<XContentType, Map<String, Object>> xContentTypeMapTuple = XContentHelper.convertToMap( xContentBuilder.bytes(), true );
        return xContentTypeMapTuple.v2();
    }


    private Collection<Object> getObjectValue( final String key1, final Map<String, Object> objectValueMap )
    {
        assertTrue( objectValueMap.containsKey( key1 ) );

        final Object objectValue = objectValueMap.get( key1 );

        assertNotNull( objectValue );
        assertTrue( objectValue instanceof Collection );

        return (Collection<Object>) objectValue;
    }
}

