package com.enonic.xp.repo.impl.index.document;

import java.time.Instant;
import java.util.List;

import org.junit.Test;

import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.repo.impl.index.IndexValueType;

import static org.junit.Assert.*;

public class IndexItemFactoryTest
{

    @Test
    public void instant_minimal()
        throws Exception
    {
        final List<IndexItem> indexItems =
            IndexItemFactory.create( "myProperty", ValueFactory.newDateTime( Instant.parse( "2015-12-11T10:29:30Z" ) ),
                                     IndexConfig.MINIMAL );

        assertEquals( 3, indexItems.size() );
        assertTypes( indexItems, IndexValueType.DATETIME, IndexValueType.STRING, IndexValueType.ORDERBY );
    }

    @Test
    public void instant_by_type()
        throws Exception
    {
        final List<IndexItem> indexItems =
            IndexItemFactory.create( "myProperty", ValueFactory.newDateTime( Instant.parse( "2015-12-11T10:29:30Z" ) ),
                                     IndexConfig.BY_TYPE );

        assertEquals( 3, indexItems.size() );
        assertTypes( indexItems, IndexValueType.DATETIME, IndexValueType.STRING, IndexValueType.ORDERBY );
    }

    @Test
    public void string_minimal()
        throws Exception
    {
        final List<IndexItem> indexItems = IndexItemFactory.create( "myProperty", ValueFactory.newString( "ost" ), IndexConfig.MINIMAL );

        assertEquals( 2, indexItems.size() );
        assertTypes( indexItems, IndexValueType.STRING, IndexValueType.ORDERBY );
    }

    @Test
    public void string_by_type()
        throws Exception
    {
        final List<IndexItem> indexItems = IndexItemFactory.create( "myProperty", ValueFactory.newString( "ost" ), IndexConfig.BY_TYPE );

        assertEquals( 6, indexItems.size() );
        assertTypes( indexItems, IndexValueType.ANALYZED, IndexValueType.STRING, IndexValueType.NGRAM, IndexValueType.ORDERBY );
    }


    private void assertAllText( final List<IndexItem> items )
    {
        boolean hasAllTextAnalyzed = false, hasAllTextNgram = false;

        for ( final IndexItem item : items )
        {
            if ( item.getKey().equals(
                NodeIndexPath.ALL_TEXT + IndexItem.INDEX_VALUE_TYPE_SEPARATOR + IndexValueType.ANALYZED.getPostfix() ) )
            {
                hasAllTextAnalyzed = true;
            }

            if ( item.getKey().equals(
                NodeIndexPath.ALL_TEXT + IndexItem.INDEX_VALUE_TYPE_SEPARATOR + IndexValueType.ANALYZED.getPostfix() ) )
            {
                hasAllTextNgram = true;
            }
        }

        if ( hasAllTextAnalyzed && hasAllTextNgram )
        {
            return;
        }

        fail( "Should have both alltext ngram and analyzed. ngram: " + hasAllTextNgram + ", fulltext: " + hasAllTextAnalyzed );
    }

    private void assertTypes( final List<IndexItem> items, IndexValueType... types )
    {
        for ( final IndexValueType type : types )
        {
            if ( !hasValueType( items, type ) )
            {
                fail( "Missing valuetype: " + type );
            }
        }
    }

    private boolean hasValueType( final List<IndexItem> items, final IndexValueType type )
    {
        for ( final IndexItem item : items )
        {
            if ( item.valueType().equals( type ) )
            {
                return true;
            }
        }

        return false;
    }


}