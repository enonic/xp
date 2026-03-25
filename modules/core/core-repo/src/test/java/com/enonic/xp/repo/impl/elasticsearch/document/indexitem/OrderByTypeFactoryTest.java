package com.enonic.xp.repo.impl.elasticsearch.document.indexitem;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexPath;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderByTypeFactoryTest
{
    @Test
    void create_returns_single_orderby_item_when_no_languages()
    {
        final IndexConfig config = IndexConfig.create().enabled( true ).build();
        final List<IndexItem<?>> items =
            IndexItemFactory.createOrderBy( IndexPath.from( "myProp" ), ValueFactory.newString( "hello" ), config );
        assertEquals( 1, items.size() );
    }

    @Test
    void create_returns_orderby_plus_language_item_for_supported_language()
    {
        final IndexConfig config = IndexConfig.create().enabled( true ).addLanguage( Locale.JAPANESE ).build();
        final List<IndexItem<?>> items =
            IndexItemFactory.createOrderBy( IndexPath.from( "myProp" ), ValueFactory.newString( "hello" ), config );
        // base _orderby item + one language-specific item
        assertEquals( 2, items.size() );
    }

    @Test
    void create_returns_single_orderby_item_for_unsupported_language()
    {
        final IndexConfig config = IndexConfig.create().enabled( true ).build();
        final List<IndexItem<?>> items =
            IndexItemFactory.createOrderBy( IndexPath.from( "myProp" ), ValueFactory.newString( "hello" ), config );
        assertEquals( 1, items.size() );
    }

    @Test
    void create_returns_correct_orderby_item_for_excess_language()
    {
        final IndexConfig config = IndexConfig.create().enabled( true ).addLanguage( Locale.forLanguageTag( "pt-BR" ) ).addLanguage( Locale.forLanguageTag( "pt" ) ).build();
        final List<IndexItem<?>> items =
            IndexItemFactory.createOrderBy( IndexPath.from( "myProp" ), ValueFactory.newString( "hello" ), config );
        assertEquals( 2, items.size() );
    }

    @Test
    void create_deduplicates_language_that_falls_back_to_orderby()
    {
        final IndexConfig config = IndexConfig.create().enabled( true ).addLanguage( Locale.ENGLISH ).addLanguage( Locale.forLanguageTag( "en-GB" ) ).build();
        final List<IndexItem<?>> items =
            IndexItemFactory.createOrderBy( IndexPath.from( "myProp" ), ValueFactory.newString( "hello" ), config );
        assertEquals( 2, items.size() );
    }
}
