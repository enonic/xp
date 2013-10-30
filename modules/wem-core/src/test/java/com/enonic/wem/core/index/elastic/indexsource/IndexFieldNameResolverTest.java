package com.enonic.wem.core.index.elastic.indexsource;

import org.joda.time.DateTime;
import org.junit.Test;

import com.enonic.wem.api.data.Value;
import com.enonic.wem.core.index.document.IndexDocumentAnalyzedItem;
import com.enonic.wem.core.index.document.IndexDocumentDateItem;
import com.enonic.wem.core.index.document.IndexDocumentGeoPointItem;
import com.enonic.wem.core.index.document.IndexDocumentNumberItem;
import com.enonic.wem.core.index.document.IndexDocumentOrderbyItem;
import com.enonic.wem.core.index.document.IndexDocumentStringItem;
import com.enonic.wem.core.index.document.IndexDocumentTokenizedItem;

import static org.junit.Assert.*;

public class IndexFieldNameResolverTest
{
    @Test
    public void create_given_string()
        throws Exception
    {
        IndexDocumentStringItem item = new IndexDocumentStringItem( "baseName", "myValue" );

        final String fieldName = IndexFieldNameResolver.create( item );

        assertEquals( "basename", fieldName );
    }


    @Test
    public void create_given_geopoint()
        throws Exception
    {
        IndexDocumentGeoPointItem item = new IndexDocumentGeoPointItem( "baseName", new Value.GeoPoint( "80, 80" ) );

        final String fieldName = IndexFieldNameResolver.create( item );

        assertEquals( "basename._geopoint", fieldName );
    }


    @Test
    public void create_given_double()
        throws Exception
    {
        IndexDocumentNumberItem item = new IndexDocumentNumberItem( "baseName", 1.0 );

        final String fieldName = IndexFieldNameResolver.create( item );

        assertEquals( "basename._number", fieldName );
    }

    @Test
    public void create_given_datetime()
        throws Exception
    {
        IndexDocumentDateItem item = new IndexDocumentDateItem( "baseName", DateTime.now() );

        final String fieldName = IndexFieldNameResolver.create( item );

        assertEquals( "basename._datetime", fieldName );
    }


    @Test
    public void create_given_analyzed()
        throws Exception
    {
        IndexDocumentAnalyzedItem item = new IndexDocumentAnalyzedItem( "baseName", "myValue" );

        final String fieldName = IndexFieldNameResolver.create( item );

        assertEquals( "basename._analyzed", fieldName );
    }

    @Test
    public void create_given_tokenized()
        throws Exception
    {
        IndexDocumentTokenizedItem item = new IndexDocumentTokenizedItem( "baseName", "myValue" );

        final String fieldName = IndexFieldNameResolver.create( item );

        assertEquals( "basename._tokenized", fieldName );
    }

    @Test
    public void create_given_orderby()
        throws Exception
    {
        IndexDocumentOrderbyItem item = new IndexDocumentOrderbyItem( "baseName", "myValue" );

        final String fieldName = IndexFieldNameResolver.create( item );

        assertEquals( "basename._orderby", fieldName );
    }


}
