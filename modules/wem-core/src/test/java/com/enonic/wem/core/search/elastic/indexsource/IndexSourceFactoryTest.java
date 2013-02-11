package com.enonic.wem.core.search.elastic.indexsource;

import java.util.Set;

import org.junit.Test;

import com.enonic.wem.core.search.IndexType;
import com.enonic.wem.core.search.indexdocument.IndexDocument;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class IndexSourceFactoryTest
{

    @Test
    public void testSimpleDocument()
        throws Exception
    {

        IndexDocument indexDocument = new IndexDocument( "id1", IndexType.CONTENT, "WEM_INDEX" );

        indexDocument.addDocumentEntry( "test1", "value1", true, true );
        indexDocument.addDocumentEntry( "test2", 1, true, true );
        indexDocument.addDocumentEntry( "test3", 2L, true, true );
        indexDocument.addDocumentEntry( "test4", 3.0, true, true );

        final IndexSource indexSource = IndexSourceFactory.create( indexDocument );

        final Set<IndexSourceEntry> indexSourceEntries = indexSource.getIndexSourceEntries();

        assertEquals( 12, indexSourceEntries.size() );
    }

    @Test
    public void testAllFieldPopulated()
        throws Exception
    {
        IndexDocument indexDocument = new IndexDocument( "id1", IndexType.CONTENT, "WEM_INDEX" );

        indexDocument.addDocumentEntry( "test1", "value1", true, false );
        indexDocument.addDocumentEntry( "test2", "value2", true, false );
        indexDocument.addDocumentEntry( "test3", "value3", true, false );
        indexDocument.addDocumentEntry( "test4", "value4", true, false );
        indexDocument.addDocumentEntry( "test4", "value5", false, false );

        final IndexSource indexSource = IndexSourceFactory.create( indexDocument );

        final Set<IndexSourceEntry> indexSourceEntries = indexSource.getIndexSourceEntries();

        assertEquals( 6, indexSourceEntries.size() );

        final IndexSourceEntry allSourceEntry = indexSource.getIndexSourceEntryWithName( "_all" );

        assertNotNull( allSourceEntry );
        final String stringValue = allSourceEntry.getValue().toString();
        assertTrue( stringValue.contains( "value1" ) );
        assertTrue( stringValue.contains( "value2" ) );
        assertTrue( stringValue.contains( "value3" ) );
        assertTrue( stringValue.contains( "value4" ) );
        assertFalse( stringValue.contains( "value5" ) );
    }

}
