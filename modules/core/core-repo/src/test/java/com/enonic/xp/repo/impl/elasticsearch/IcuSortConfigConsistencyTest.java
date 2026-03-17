package com.enonic.xp.repo.impl.elasticsearch;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies that search-settings.json and search-mapping.json are consistent
 * for all ICU language-aware sort entries. For every icu_collation_XX filter defined
 * in settings there must be a matching icu_sort_XX analyzer and a template_orderby_XX
 * dynamic template in mapping that wires them together correctly.
 */
public class IcuSortConfigConsistencyTest
{
    private static final String SETTINGS_PATH = "/com/enonic/xp/repo/impl/repository/index/settings/default/search-settings.json";

    private static final String MAPPING_PATH = "/com/enonic/xp/repo/impl/repository/index/mapping/default/search-mapping.json";

    private static final String FILTER_PREFIX = "icu_collation_";

    private static final String ANALYZER_PREFIX = "icu_sort_";

    private static final String TEMPLATE_PREFIX = "template_orderby_";

    @Test
    public void allIcuLanguageEntriesAreConsistent()
        throws Exception
    {
        final ObjectMapper mapper = new ObjectMapper();

        final JsonNode settings;
        try (InputStream in = getClass().getResourceAsStream( SETTINGS_PATH ))
        {
            assertNotNull( in, "search-settings.json not found on classpath" );
            settings = mapper.readTree( in );
        }

        final JsonNode mapping;
        try (InputStream in = getClass().getResourceAsStream( MAPPING_PATH ))
        {
            assertNotNull( in, "search-mapping.json not found on classpath" );
            mapping = mapper.readTree( in );
        }

        final JsonNode filters = settings.path( "analysis" ).path( "filter" );
        final JsonNode analyzers = settings.path( "analysis" ).path( "analyzer" );
        final JsonNode dynamicTemplates = mapping.path( "_default_" ).path( "dynamic_templates" );

        // Collect language codes from filters
        final List<String> languageCodes = new ArrayList<>();
        filters.fieldNames().forEachRemaining( name -> {
            if ( name.startsWith( FILTER_PREFIX ) )
            {
                languageCodes.add( name.substring( FILTER_PREFIX.length() ) );
            }
        } );

        assertThat( languageCodes.size()).isEqualTo( 25 );

        // Build lookup for template analyzer references
        final java.util.Map<String, String> templateAnalyzerByLang = new java.util.HashMap<>();
        dynamicTemplates.forEach( templateNode -> {
            templateNode.fieldNames().forEachRemaining( templateName -> {
                if ( templateName.startsWith( TEMPLATE_PREFIX ) )
                {
                    final String lang = templateName.substring( TEMPLATE_PREFIX.length() );
                    final String analyzer = templateNode.path( templateName ).path( "mapping" ).path( "analyzer" ).asText( null );
                    templateAnalyzerByLang.put( lang, analyzer );
                }
            } );
        } );

        for ( final String lang : languageCodes )
        {
            final String filterName = FILTER_PREFIX + lang;
            final String analyzerName = ANALYZER_PREFIX + lang;
            final String templateName = TEMPLATE_PREFIX + lang;

            // icu_sort_XX analyzer must exist in settings
            assertTrue( analyzers.has( analyzerName ),
                        "Missing analyzer '" + analyzerName + "' in search-settings.json for language '" + lang + "'" );

            // icu_sort_XX analyzer must reference exactly icu_collation_XX filter
            final JsonNode analyzerFilters = analyzers.path( analyzerName ).path( "filter" );
            assertTrue( analyzerFilters.isArray() && analyzerFilters.size() == 1,
                        "Analyzer '" + analyzerName + "' must have exactly one filter" );
            assertEquals( filterName, analyzerFilters.get( 0 ).asText(),
                          "Analyzer '" + analyzerName + "' must reference filter '" + filterName + "'" );

            // template_orderby_XX must exist in mapping
            assertTrue( templateAnalyzerByLang.containsKey( lang ),
                        "Missing dynamic template '" + templateName + "' in search-mapping.json for language '" + lang + "'" );

            // template_orderby_XX must reference icu_sort_XX analyzer
            assertEquals( analyzerName, templateAnalyzerByLang.get( lang ),
                          "Template '" + templateName + "' must use analyzer '" + analyzerName + "'" );
        }

        // Also verify symmetry: every template_orderby_XX must have a corresponding filter
        for ( final String lang : templateAnalyzerByLang.keySet() )
        {
            assertTrue( filters.has( FILTER_PREFIX + lang ),
                        "Template 'template_orderby_" + lang + "' has no matching filter '" + FILTER_PREFIX + lang + "'" );
        }
    }
}
