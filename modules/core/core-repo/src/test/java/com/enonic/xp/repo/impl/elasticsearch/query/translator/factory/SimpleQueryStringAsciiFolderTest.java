package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class SimpleQueryStringAsciiFolderTest
{
    @Test
    void fuzzy_term()
    {
        assertEquals( "Gronnsak~2", SimpleQueryStringAsciiFolder.foldFuzzyTerms( "Grønnsak~2" ) );
        assertEquals( "gronnsak~", SimpleQueryStringAsciiFolder.foldFuzzyTerms( "grønnsak~" ) );
        assertEquals( "aeble~1", SimpleQueryStringAsciiFolder.foldFuzzyTerms( "æble~1" ) );
    }

    @Test
    void nothing_to_fold()
    {
        assertNull( SimpleQueryStringAsciiFolder.foldFuzzyTerms( null ) );
        assertSame( "", SimpleQueryStringAsciiFolder.foldFuzzyTerms( "" ) );

        final String asciiOnly = "levvenstein~2 fsik~2";
        assertSame( asciiOnly, SimpleQueryStringAsciiFolder.foldFuzzyTerms( asciiOnly ) );
    }

    @Test
    void analyzed_terms_untouched()
    {
        assertEquals( "grønnsak", SimpleQueryStringAsciiFolder.foldFuzzyTerms( "grønnsak" ) );
        assertEquals( "grønns*", SimpleQueryStringAsciiFolder.foldFuzzyTerms( "grønns*" ) );
        assertEquals( "\"grønne saker\"", SimpleQueryStringAsciiFolder.foldFuzzyTerms( "\"grønne saker\"" ) );
        assertEquals( "\"grønne saker\"~2", SimpleQueryStringAsciiFolder.foldFuzzyTerms( "\"grønne saker\"~2" ) );
    }

    @Test
    void only_fuzzy_terms_folded()
    {
        assertEquals( "grønnsaker gronnsak~2", SimpleQueryStringAsciiFolder.foldFuzzyTerms( "grønnsaker grønnsak~2" ) );
        assertEquals( "gronnsak~2 grønnsaker", SimpleQueryStringAsciiFolder.foldFuzzyTerms( "grønnsak~2 grønnsaker" ) );
        assertEquals( "-gronnsak~2 +løk", SimpleQueryStringAsciiFolder.foldFuzzyTerms( "-grønnsak~2 +løk" ) );
        assertEquals( "(gronnsak~2|løk) ost", SimpleQueryStringAsciiFolder.foldFuzzyTerms( "(grønnsak~2|løk) ost" ) );
        assertEquals( "\"grønne saker\"~2 gronnsak~2", SimpleQueryStringAsciiFolder.foldFuzzyTerms( "\"grønne saker\"~2 grønnsak~2" ) );
        assertEquals( "grønnsak lok~2", SimpleQueryStringAsciiFolder.foldFuzzyTerms( "grønnsak løk~2" ) );
    }

    @Test
    void escaping_kept()
    {
        // an escaped diacritic is still a diacritic
        assertEquals( "gr\\onnsak~2", SimpleQueryStringAsciiFolder.foldFuzzyTerms( "gr\\ønnsak~2" ) );

        // an escaped tilde is not a fuzzy expression, the term is analyzed and folded by Elasticsearch
        assertEquals( "grønnsak\\~2 ", SimpleQueryStringAsciiFolder.foldFuzzyTerms( "grønnsak\\~2 " ) );
    }

    @Test
    void folding_does_not_introduce_operators()
    {
        // « and » fold to a quote, which would have turned the term into a phrase
        assertEquals( "«gronnsak»~2", SimpleQueryStringAsciiFolder.foldFuzzyTerms( "«grønnsak»~2" ) );

        // a non-breaking space folds to a space, which would have split the term in two
        assertEquals( "gronn\u00A0sak~2", SimpleQueryStringAsciiFolder.foldFuzzyTerms( "grønn\u00A0sak~2" ) );
    }

    @Test
    void leading_tilde_is_part_of_the_term()
    {
        assertEquals( "~løk", SimpleQueryStringAsciiFolder.foldFuzzyTerms( "~løk" ) );
    }
}
