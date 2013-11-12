package com.enonic.wem.core.index.elastic.indexsource;

import java.util.Collection;
import java.util.Map;

import org.elasticsearch.common.collect.Tuple;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentHelper;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Test;

import com.enonic.wem.core.index.Index;
import com.enonic.wem.core.index.IndexConstants;
import com.enonic.wem.core.index.IndexType;
import com.enonic.wem.core.index.document.IndexDocument2;

import static org.junit.Assert.*;

public class XContentBuilderFactoryTest
{

    @Test
    public void create_given_indexdocument_analyzer()
        throws Exception
    {
        IndexDocument2 indexDocument = IndexDocument2.newIndexDocument().
            index( Index.NODB ).
            indexType( IndexType.NODE ).
            analyzer( "myAnalyzer" ).
            build();

        final XContentBuilder xContentBuilder = XContentBuilderFactory.create( indexDocument );

        final Map<String, Object> objectMap = getObjectMap( xContentBuilder );

        final Object objectValue = objectMap.get( IndexConstants.ANALYZER_VALUE_FIELD );

        assertNotNull( objectValue );
        assertTrue( objectValue instanceof String );
        assertEquals( "myAnalyzer", objectValue );
    }

    @Test
    public void create_given_indexdocument_arrayvalues()
        throws Exception
    {
        /*
        IndexDocument2 indexDocument = IndexDocument2.newIndexDocument().
            index( Index.NODB ).
            indexType( IndexType.NODE ).
            addEntry( new IndexDocumentStringItem( "myField", "myValue1" ) ).
            addEntry( new IndexDocumentStringItem( "myField", "myValue2" ) ).
            addEntry( new IndexDocumentNumberItem( "myNumericField", 1.0 ) ).
            addEntry( new IndexDocumentNumberItem( "myNumericField", 2.0 ) ).
            addEntry( new IndexDocumentDateItem( "myDateField", DateTime.now() ) ).
            addEntry( new IndexDocumentDateItem( "myDateField", DateTime.now() ) ).
            addEntry( new IndexDocumentGeoPointItem( "myGeoPoint", new Value.GeoPoint( "80,80" ) ) ).
            addEntry( new IndexDocumentGeoPointItem( "myGeoPoint", new Value.GeoPoint( "81,81" ) ) ).
            build();

        final XContentBuilder xContentBuilder = XContentBuilderFactory.create( indexDocument );

        final Map<String, Object> objectMap = getObjectMap( xContentBuilder );

        final Collection<Object> objectValue = getObjectValue( "myfield", objectMap );
        assertEquals( 2, objectValue.size() );

        final Collection<Object> numericObjectValue = getObjectValue( "mynumericfield._number", objectMap );
        assertEquals( 2, numericObjectValue.size() );

        final Collection<Object> dateObjectValue = getObjectValue( "mydatefield._datetime", objectMap );
        assertEquals( 2, dateObjectValue.size() );

        final Collection<Object> geoPointObjectValue = getObjectValue( "mygeopoint._geopoint", objectMap );
        assertEquals( 2, geoPointObjectValue.size() );
   */
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

