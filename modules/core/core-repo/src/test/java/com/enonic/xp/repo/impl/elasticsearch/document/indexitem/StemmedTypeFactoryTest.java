package com.enonic.xp.repo.impl.elasticsearch.document.indexitem;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexPath;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StemmedTypeFactoryTest
{
    @Test
    void create_returns_empty_when_no_languages()
    {
        // isStemmed() returns false when no languages configured
        final IndexConfig config = IndexConfig.create().enabled( true ).build();
        final List<IndexItem<?>> items =
            IndexItemFactory.createStemmed( IndexPath.from( "myProp" ), ValueFactory.newString( "hello" ), config );
        assertTrue( items.isEmpty() );
    }

    @Test
    void create_returns_stemmed_item_for_supported_language()
    {
        final IndexConfig config = IndexConfig.create().enabled( true ).addLanguage( Locale.forLanguageTag( "en" ) ).build();
        final List<IndexItem<?>> items =
            IndexItemFactory.createStemmed( IndexPath.from( "myProp" ), ValueFactory.newString( "hello" ), config );
        assertEquals( 1, items.size() );
    }

    @Test
    void create_empty_for_unsupported_language()
    {
        final IndexConfig config = IndexConfig.create().enabled( true ).addLanguage( Locale.forLanguageTag( "xyz" ) ).build();
        assertThat( IndexItemFactory.createStemmed( IndexPath.from( "myProp" ), ValueFactory.newString( "hello" ), config ) ).isEmpty();
    }

    @Test
    void create_accepts_pt_BR_case_insensitive()
    {
        final IndexConfig config = IndexConfig.create().enabled( true ).addLanguage( Locale.forLanguageTag( "pt-BR" ) ).build();
        final List<IndexItem<?>> items =
            IndexItemFactory.createStemmed( IndexPath.from( "myProp" ), ValueFactory.newString( "hello" ), config );
        assertEquals( 1, items.size() );
    }
}
