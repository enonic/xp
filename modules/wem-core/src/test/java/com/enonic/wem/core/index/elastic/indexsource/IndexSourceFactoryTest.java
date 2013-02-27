package com.enonic.wem.core.index.elastic.indexsource;

import java.util.Collection;
import java.util.Date;

import org.joda.time.DateTime;
import org.junit.Test;

import com.enonic.wem.core.index.IndexConstants;
import com.enonic.wem.core.index.IndexType;
import com.enonic.wem.core.index.indexdocument.IndexDocument;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.*;

public class IndexSourceFactoryTest
{

    @Test
    public void testNumberDateAndStringsIntoAllUserdataField()
        throws Exception
    {
        final Date now = DateTime.now().toDate();

        IndexDocument indexDocument = new IndexDocument( "id1", IndexType.CONTENT, "WEM_INDEX" );

        indexDocument.addDocumentEntry( "test1", "value1", true, true );
        indexDocument.addDocumentEntry( "test2", 1, true, true );
        indexDocument.addDocumentEntry( "test3", 2L, true, true );
        indexDocument.addDocumentEntry( "test4", 3.0, true, true );
        indexDocument.addDocumentEntry( "test5", now, true, true );

        final IndexSource indexSource = IndexSourceFactory.create( indexDocument );

        final IndexSourceEntry dateAllField = indexSource.getIndexSourceEntryWithName( IndexConstants.ALL_USERDATA_DATE_FIELD );
        final IndexSourceEntry numberAllField = indexSource.getIndexSourceEntryWithName( IndexConstants.ALL_USERDATA_NUMBER_FIELD );
        final IndexSourceEntry stringAllField = indexSource.getIndexSourceEntryWithName( IndexConstants.ALL_USERDATA_STRING_FIELD );

        final Collection<Date> dateValues = (Collection<Date>) dateAllField.getValue();
        final Collection<Number> numberValues = (Collection<Number>) numberAllField.getValue();
        final Collection<String> stringValues = (Collection<String>) stringAllField.getValue();

        assertTrue( stringValues.contains( "value1" ) );

        assertTrue( numberValues.contains( 1 ) );
        assertTrue( numberValues.contains( 2L ) );
        assertTrue( numberValues.contains( 3.0 ) );

        assertTrue( dateValues.contains( now ) );
    }

    @Test
    public void testNotIncludeEntriesMarkedAsNotInAllUserdataField()
        throws Exception
    {
        IndexDocument indexDocument = new IndexDocument( "id1", IndexType.CONTENT, "WEM_INDEX" );

        indexDocument.addDocumentEntry( "test1", "value1", true, false );
        indexDocument.addDocumentEntry( "test2", "value2", false, false );

        final IndexSource indexSource = IndexSourceFactory.create( indexDocument );

        final IndexSourceEntry stringAllField = indexSource.getIndexSourceEntryWithName( IndexConstants.ALL_USERDATA_STRING_FIELD );

        final Collection<String> stringValues = (Collection<String>) stringAllField.getValue();

        assertTrue( stringValues.contains( "value1" ) );
        assertFalse( stringValues.contains( "value2" ) );
    }

    @Test
    public void testArrayValuesIntoAllUserdataField()
        throws Exception
    {
        final Date date1 = DateTime.parse( "2001" ).toDate();
        final Date date2 = DateTime.parse( "2002" ).toDate();
        final Date date3 = DateTime.parse( "2003" ).toDate();

        IndexDocument indexDocument = new IndexDocument( "id1", IndexType.CONTENT, "WEM_INDEX" );

        indexDocument.addDocumentEntry( "test1", new String[]{"value1", "value2", "value3"}, true, false );
        indexDocument.addDocumentEntry( "test2", new Double[]{1.0, 2.0, 3.0}, true, false );
        indexDocument.addDocumentEntry( "test3", new Date[]{date1, date2, date3}, true, false );

        final IndexSource indexSource = IndexSourceFactory.create( indexDocument );

        final IndexSourceEntry dateAllField = indexSource.getIndexSourceEntryWithName( IndexConstants.ALL_USERDATA_DATE_FIELD );
        final IndexSourceEntry numberAllField = indexSource.getIndexSourceEntryWithName( IndexConstants.ALL_USERDATA_NUMBER_FIELD );
        final IndexSourceEntry stringAllField = indexSource.getIndexSourceEntryWithName( IndexConstants.ALL_USERDATA_STRING_FIELD );

        final Collection<Date> dateValues = (Collection<Date>) dateAllField.getValue();
        final Collection<Number> numberValues = (Collection<Number>) numberAllField.getValue();
        final Collection<String> stringValues = (Collection<String>) stringAllField.getValue();

        assertTrue( dateValues.contains( date1 ) );
        assertTrue( dateValues.contains( date2 ) );
        assertTrue( dateValues.contains( date3 ) );

        assertTrue( numberValues.contains( 1.0 ) );
        assertTrue( numberValues.contains( 2.0 ) );
        assertTrue( numberValues.contains( 3.0 ) );

        assertTrue( stringValues.contains( "value1" ) );
        assertTrue( stringValues.contains( "value2" ) );
        assertTrue( stringValues.contains( "value3" ) );
    }

}
