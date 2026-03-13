package com.enonic.xp.repo.impl.elasticsearch.document.indexitem;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexPath;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        final IndexConfig config = IndexConfig.create().enabled( true ).addLanguage( Locale.ENGLISH ).build();
        final List<IndexItem<?>> items =
            IndexItemFactory.createOrderBy( IndexPath.from( "myProp" ), ValueFactory.newString( "hello" ), config );
        // base _orderby item + one language-specific item
        assertEquals( 2, items.size() );
    }

    @Test
    void create_throws_for_unsupported_language()
    {
        final IndexConfig config = IndexConfig.create().enabled( true ).addLanguage(Locale.forLanguageTag( "xyz") ).build();
        assertThrows( IllegalArgumentException.class,
                      () -> IndexItemFactory.createOrderBy( IndexPath.from( "myProp" ), ValueFactory.newString( "hello" ), config ) );
    }

    @Test
    void create_throws_for_unsupported_sort_language()
    {
        final IndexConfig config = IndexConfig.create().enabled( true ).addLanguage( Locale.forLanguageTag( "pt-BR" ) ).build();
        assertThrows( IllegalArgumentException.class,
                      () -> IndexItemFactory.createOrderBy( IndexPath.from( "myProp" ), ValueFactory.newString( "hello" ), config ) );
    }
}
