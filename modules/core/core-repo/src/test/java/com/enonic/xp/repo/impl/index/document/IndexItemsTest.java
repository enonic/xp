package com.enonic.xp.repo.impl.index.document;

import java.util.Collection;

import org.junit.Test;

import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.repo.impl.index.IndexValueType;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

public class IndexItemsTest
{
    @Test
    public void single_string()
        throws Exception
    {
        final IndexItems indexItems = new IndexItems();

        indexItems.add( "myItem", ValueFactory.newString( "ost" ), IndexConfig.MINIMAL );

        final Collection<IndexValue> values = indexItems.get( "myitem" );

        assertTrue( values.size() == 1 );

        final IndexValue value = values.iterator().next();

        assertTrue( value instanceof IndexValueString );

        assertEquals( "ost", value.getValue() );
    }

    @Test
    public void multiple_strings()
        throws Exception
    {
        final IndexItems indexItems = new IndexItems();

        indexItems.add( "myItem", ValueFactory.newString( "ost" ), IndexConfig.MINIMAL );
        indexItems.add( "myItem", ValueFactory.newString( "fisk" ), IndexConfig.MINIMAL );

        final Collection<IndexValue> values = indexItems.get( "myitem" );

        assertTrue( values.size() == 2 );

        final IndexValue value = values.iterator().next();

        assertTrue( value instanceof IndexValueString );

        assertEquals( "ost", value.getValue() );
    }


    @Test
    public void single_orderby_value()
        throws Exception
    {
        final IndexItems indexItems = new IndexItems();

        indexItems.add( "myItem", ValueFactory.newString( "ost" ), IndexConfig.MINIMAL );
        indexItems.add( "myItem", ValueFactory.newString( "fisk" ), IndexConfig.MINIMAL );

        final Collection<IndexValue> values =
            indexItems.get( "myitem" + IndexItem.INDEX_VALUE_TYPE_SEPARATOR + IndexValueType.ORDERBY.getPostfix() );

        assertEquals( 1, values.size() );

        final IndexValue value = values.iterator().next();

        assertTrue( value instanceof IndexValueString );

        assertEquals( "ost", value.getValue() );
    }

}