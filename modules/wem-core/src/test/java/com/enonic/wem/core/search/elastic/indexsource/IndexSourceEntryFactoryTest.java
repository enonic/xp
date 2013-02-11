package com.enonic.wem.core.search.elastic.indexsource;

import java.util.Iterator;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Test;

import com.enonic.wem.core.search.indexdocument.IndexDocumentEntry;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

public class IndexSourceEntryFactoryTest
{

    private IndexSourceEntryFactory indexSourceEntryFactory = new IndexSourceEntryFactory();

    @Test
    public void testIntField()
        throws Exception
    {
        IndexDocumentEntry indexDocumentEntry = new IndexDocumentEntry( "test", 1, true, true );

        final Set<IndexSourceEntry> indexSourceEntries = indexSourceEntryFactory.create( indexDocumentEntry );

        assertEquals( 3, indexSourceEntries.size() );
        expectKeys( indexSourceEntries, new String[]{"test", "test" + IndexSourceEntryFactory.NUMERIC_FIELD_POSTFIX,
            "test" + IndexSourceEntryFactory.ORDERBY_FIELD_POSTFIX} );
    }

    @Test
    public void testLongField()
        throws Exception
    {
        IndexDocumentEntry indexDocumentEntry = new IndexDocumentEntry( "test", 1L, true, true );

        final Set<IndexSourceEntry> indexSourceEntries = indexSourceEntryFactory.create( indexDocumentEntry );

        assertEquals( 3, indexSourceEntries.size() );
        expectKeys( indexSourceEntries, new String[]{"test", "test" + IndexSourceEntryFactory.NUMERIC_FIELD_POSTFIX,
            "test" + IndexSourceEntryFactory.ORDERBY_FIELD_POSTFIX} );
    }

    @Test
    public void testDoubleField()
        throws Exception
    {
        IndexDocumentEntry indexDocumentEntry = new IndexDocumentEntry( "test", 1.0, true, true );

        final Set<IndexSourceEntry> indexSourceEntries = indexSourceEntryFactory.create( indexDocumentEntry );

        assertEquals( 3, indexSourceEntries.size() );

        expectKeys( indexSourceEntries, new String[]{"test", "test" + IndexSourceEntryFactory.NUMERIC_FIELD_POSTFIX,
            "test" + IndexSourceEntryFactory.ORDERBY_FIELD_POSTFIX} );
    }

    @Test
    public void testStringField()
        throws Exception
    {
        IndexDocumentEntry indexDocumentEntry = new IndexDocumentEntry( "test", "value", true, true );

        final Set<IndexSourceEntry> indexSourceEntries = indexSourceEntryFactory.create( indexDocumentEntry );

        assertEquals( 2, indexSourceEntries.size() );

        expectKeys( indexSourceEntries, new String[]{"test", "test" + IndexSourceEntryFactory.ORDERBY_FIELD_POSTFIX} );
    }

    @Test
    public void testStringField_no_orderby()
        throws Exception
    {
        IndexDocumentEntry indexDocumentEntry = new IndexDocumentEntry( "test", "value", false, false );

        final Set<IndexSourceEntry> indexSourceEntries = indexSourceEntryFactory.create( indexDocumentEntry );

        assertEquals( 1, indexSourceEntries.size() );

        expectKeys( indexSourceEntries, new String[]{"test"} );
    }

    @Test
    public void testDateField()
    {
        IndexDocumentEntry indexDocumentEntry = new IndexDocumentEntry( "myDate", DateTime.now().toDate(), false, false );
        final Set<IndexSourceEntry> indexSourceEntries = indexSourceEntryFactory.create( indexDocumentEntry );

        assertEquals( 2, indexSourceEntries.size() );

        expectKeys( indexSourceEntries, new String[]{"myDate", "myDate" + IndexSourceEntryFactory.DATE_FIELD_POSTFIX} );

    }

    private void expectKeys( final Set<IndexSourceEntry> indexSourceEntries, final String[] expectedKeys )
    {
        for ( String expectedKey : expectedKeys )
        {
            final Iterator<IndexSourceEntry> iterator = indexSourceEntries.iterator();

            while ( iterator.hasNext() )
            {
                if ( iterator.next().getKey().equals( expectedKey ) )
                {
                    return;
                }

            }

            fail( "missing expected key: " + expectedKey );
        }
    }

}
