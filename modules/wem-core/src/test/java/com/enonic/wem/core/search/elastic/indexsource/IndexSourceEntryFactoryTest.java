package com.enonic.wem.core.search.elastic.indexsource;

import java.util.Set;

import org.junit.Test;

import com.enonic.wem.core.search.indexdocument.IndexDocumentEntry;

import static junit.framework.Assert.assertEquals;

public class IndexSourceEntryFactoryTest
{

    private IndexSourceEntryFactory indexSourceEntryFactory = new IndexSourceEntryFactory();

    @Test
    public void testIntField()
        throws Exception
    {
        IndexDocumentEntry indexDocumentEntry = new IndexDocumentEntry( "test", 1, true, true );

        final Set<IndexSourceEntry> indexSourceEntries = indexSourceEntryFactory.createIndexSourceEntries( indexDocumentEntry );

        assertEquals( 3, indexSourceEntries.size() );
    }

    @Test
    public void testLongField()
        throws Exception
    {
        IndexDocumentEntry indexDocumentEntry = new IndexDocumentEntry( "test", 1L, true, true);

        final Set<IndexSourceEntry> indexSourceEntries = indexSourceEntryFactory.createIndexSourceEntries( indexDocumentEntry );

        assertEquals( 3, indexSourceEntries.size() );
    }

    @Test
    public void testDoubleField()
        throws Exception
    {
        IndexDocumentEntry indexDocumentEntry = new IndexDocumentEntry( "test", 1.0, true, true);

        final Set<IndexSourceEntry> indexSourceEntries = indexSourceEntryFactory.createIndexSourceEntries( indexDocumentEntry );

        assertEquals( 3, indexSourceEntries.size() );
    }

    @Test
    public void testStringField()
        throws Exception
    {
        IndexDocumentEntry indexDocumentEntry = new IndexDocumentEntry( "test", "value", true, true);

        final Set<IndexSourceEntry> indexSourceEntries = indexSourceEntryFactory.createIndexSourceEntries( indexDocumentEntry );

        assertEquals( 2, indexSourceEntries.size() );
    }

    @Test
    public void testStringField_no_orderby()
        throws Exception
    {
        IndexDocumentEntry indexDocumentEntry = new IndexDocumentEntry( "test", "value", false, false );

        final Set<IndexSourceEntry> indexSourceEntries = indexSourceEntryFactory.createIndexSourceEntries( indexDocumentEntry );

        assertEquals( 1, indexSourceEntries.size() );
    }

}
