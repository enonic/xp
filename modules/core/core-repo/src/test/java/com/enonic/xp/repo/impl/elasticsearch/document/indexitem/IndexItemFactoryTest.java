package com.enonic.xp.repo.impl.elasticsearch.document.indexitem;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.repo.impl.index.IndexValueType;
import com.enonic.xp.util.GeoPoint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class IndexItemFactoryTest
{
    @Test
    void instant_minimal()
    {
        final List<IndexItem> indexItems =
            IndexItemFactory.create( IndexPath.from( "myProperty" ), ValueFactory.newDateTime( Instant.parse( "2015-12-11T10:29:30Z" ) ),
                                     createDefaultDocument( IndexConfig.MINIMAL ) );

        assertEquals( 3, indexItems.size() );
        assertTypes( indexItems, IndexValueType.DATETIME, IndexValueType.STRING, IndexValueType.ORDERBY );
    }

    @Test
    void instant_by_type()
    {
        final List<IndexItem> indexItems =
            IndexItemFactory.create( IndexPath.from( "myProperty" ), ValueFactory.newDateTime( Instant.parse( "2015-12-11T10:29:30Z" ) ),
                                     createDefaultDocument( IndexConfig.BY_TYPE ) );

        assertEquals( 3, indexItems.size() );
        assertTypes( indexItems, IndexValueType.DATETIME, IndexValueType.STRING, IndexValueType.ORDERBY );
    }

    @Test
    void string_minimal()
    {
        final List<IndexItem> indexItems = IndexItemFactory.create( IndexPath.from( "myProperty" ), ValueFactory.newString( "ost" ),
                                                                    createDefaultDocument( IndexConfig.MINIMAL ) );

        assertEquals( 2, indexItems.size() );
        assertTypes( indexItems, IndexValueType.STRING, IndexValueType.ORDERBY );
    }

    @Test
    void string_by_type()
    {
        final List<IndexItem> indexItems = IndexItemFactory.create( IndexPath.from( "myProperty" ), ValueFactory.newString( "ost" ),
                                                                    createDefaultDocument( IndexConfig.BY_TYPE ) );

        assertEquals( 6, indexItems.size() );
        assertTypes( indexItems, IndexValueType.ANALYZED, IndexValueType.STRING, IndexValueType.NGRAM, IndexValueType.ORDERBY );
    }

    @Test
    void double_minimal()
    {
        final List<IndexItem> indexItems = IndexItemFactory.create( IndexPath.from( "myProperty" ), ValueFactory.newDouble( 12.3 ),
                                                                    createDefaultDocument( IndexConfig.MINIMAL ) );
        assertEquals( 3, indexItems.size() );
        assertTypes( indexItems, IndexValueType.NUMBER, IndexValueType.STRING, IndexValueType.ORDERBY );
    }

    @Test
    void geopoint_minimal()
    {
        final List<IndexItem> indexItems =
            IndexItemFactory.create( IndexPath.from( "myProperty" ), ValueFactory.newGeoPoint( new GeoPoint( 80, 80 ) ),
                                     createDefaultDocument( IndexConfig.MINIMAL ) );
        assertEquals( 3, indexItems.size() );
        assertTypes( indexItems, IndexValueType.GEO_POINT, IndexValueType.STRING, IndexValueType.ORDERBY );
    }


    private void assertAllText( final List<IndexItem> items )
    {
        boolean hasAllTextAnalyzed = false, hasAllTextNgram = false;

        for ( final IndexItem item : items )
        {
            if ( item.getPath().equals(
                NodeIndexPath.ALL_TEXT + IndexItem.INDEX_VALUE_TYPE_SEPARATOR + IndexValueType.ANALYZED.getPostfix() ) )
            {
                hasAllTextAnalyzed = true;
            }

            if ( item.getPath().equals(
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

    private IndexConfigDocument createDefaultDocument( final IndexConfig indexConfig )
    {
        final PatternIndexConfigDocument.Builder builder = PatternIndexConfigDocument.create().defaultConfig( indexConfig );
        return builder.build();
    }

}
