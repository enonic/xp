package com.enonic.xp.repo.impl.elasticsearch.document.indexitem;

import java.util.Collection;

import org.junit.Test;

import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.repo.impl.index.IndexValueType;

import static org.junit.Assert.*;

public class IndexItemsTest
{
    @Test
    public void single_string()
        throws Exception
    {
        final IndexItems indexItems = IndexItems.create().
            add( IndexPath.from( "myItem" ), ValueFactory.newString( "ost" ), createDefaultDocument( IndexConfig.MINIMAL ) ).
            build();

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
        final IndexItems indexItems = IndexItems.create().
            add( IndexPath.from( "myItem" ), ValueFactory.newString( "ost" ), createDefaultDocument( IndexConfig.MINIMAL ) ).
            add( IndexPath.from( "myItem" ), ValueFactory.newString( "fisk" ), createDefaultDocument( IndexConfig.MINIMAL ) ).
            build();

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
        final IndexItems indexItems = IndexItems.create().
            add( IndexPath.from( "myItem" ), ValueFactory.newString( "ost" ), createDefaultDocument( IndexConfig.MINIMAL ) ).
            add( IndexPath.from( "myItem" ), ValueFactory.newString( "fisk" ), createDefaultDocument( IndexConfig.MINIMAL ) ).
            build();

        final Collection<IndexValue> values =
            indexItems.get( "myitem" + IndexItem.INDEX_VALUE_TYPE_SEPARATOR + IndexValueType.ORDERBY.getPostfix() );

        assertEquals( 1, values.size() );

        final IndexValue value = values.iterator().next();

        assertTrue( value instanceof IndexValueString );

        assertEquals( "ost", value.getValue() );
    }

    private IndexConfigDocument createDefaultDocument( final IndexConfig indexConfig )
    {
        final PatternIndexConfigDocument.Builder builder = PatternIndexConfigDocument.create().defaultConfig( indexConfig );
        return builder.build();
    }
}