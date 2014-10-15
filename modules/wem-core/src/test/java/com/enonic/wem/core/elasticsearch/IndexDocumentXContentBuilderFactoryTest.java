package com.enonic.wem.core.elasticsearch;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.elasticsearch.common.collect.Tuple;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentHelper;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Test;

import com.enonic.wem.api.data.Value;
import com.enonic.wem.core.index.IndexConstants;
import com.enonic.wem.core.index.document.IndexDocument;
import com.enonic.wem.core.index.document.IndexDocumentDateItem;
import com.enonic.wem.core.index.document.IndexDocumentGeoPointItem;
import com.enonic.wem.core.index.document.IndexDocumentItemPath;
import com.enonic.wem.core.index.document.IndexDocumentNumberItem;
import com.enonic.wem.core.index.document.IndexDocumentOrderbyItem;
import com.enonic.wem.core.index.document.IndexDocumentStringItem;

import static org.junit.Assert.*;

public class IndexDocumentXContentBuilderFactoryTest
{

    @Test
    public void create_given_indexdocument_analyzer()
        throws Exception
    {
        IndexDocument indexDocument = IndexDocument.newIndexDocument().
            index( "testindex" ).
            indexType( "test" ).
            analyzer( "myAnalyzer" ).
            build();

        final XContentBuilder xContentBuilder = IndexDocumentXContentBuilderFactory.create( indexDocument );

        final Map<String, Object> objectMap = getObjectMap( xContentBuilder );

        final Object objectValue = objectMap.get( IndexConstants.ANALYZER_VALUE_FIELD );

        assertNotNull( objectValue );
        assertTrue( objectValue instanceof String );
        assertEquals( "myAnalyzer", objectValue );
    }

    @Test
    public void multiple_orderby_entries_gives_one()
    {
        IndexDocument indexDocument = IndexDocument.newIndexDocument().
            index( "testindex" ).
            indexType( "test" ).
            addEntry( new IndexDocumentStringItem( IndexDocumentItemPath.from( "myField" ), "myValue1" ) ).
            addEntry( new IndexDocumentStringItem( IndexDocumentItemPath.from( "myField" ), "myValue2" ) ).
            addEntry( new IndexDocumentOrderbyItem( IndexDocumentItemPath.from( "myField" ), "myOrderByValue1" ) ).
            addEntry( new IndexDocumentOrderbyItem( IndexDocumentItemPath.from( "myField" ), "myOrderByValue2" ) ).
            build();

        final XContentBuilder xContentBuilder = IndexDocumentXContentBuilderFactory.create( indexDocument );

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

        IndexDocument indexDocument = IndexDocument.newIndexDocument().
            index( "testindex" ).
            indexType( "test" ).
            addEntry( new IndexDocumentStringItem( IndexDocumentItemPath.from( "myField" ), "myValue1" ) ).
            addEntry( new IndexDocumentStringItem( IndexDocumentItemPath.from( "myField" ), "myValue2" ) ).
            addEntry( new IndexDocumentNumberItem( IndexDocumentItemPath.from( "myNumericField" ), 1.0 ) ).
            addEntry( new IndexDocumentNumberItem( IndexDocumentItemPath.from( "myNumericField" ), 2.0 ) ).
            addEntry( new IndexDocumentDateItem( IndexDocumentItemPath.from( "myDateField" ), Instant.now() ) ).
            addEntry( new IndexDocumentDateItem( IndexDocumentItemPath.from( "myDateField" ), Instant.now() ) ).
            addEntry(
                new IndexDocumentGeoPointItem( IndexDocumentItemPath.from( "myGeoPoint" ), Value.newGeoPoint( "80,80" ).toString() ) ).
            addEntry(
                new IndexDocumentGeoPointItem( IndexDocumentItemPath.from( "myGeoPoint" ), Value.newGeoPoint( "81,81" ).toString() ) ).
            build();

        final XContentBuilder xContentBuilder = IndexDocumentXContentBuilderFactory.create( indexDocument );

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

