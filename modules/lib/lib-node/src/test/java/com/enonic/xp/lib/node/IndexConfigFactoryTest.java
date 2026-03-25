package com.enonic.xp.lib.node;

import java.util.List;
import java.util.Locale;
import java.util.SortedSet;

import org.junit.jupiter.api.Test;

import com.enonic.xp.core.internal.json.JsonHelper;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.index.IndexValueProcessor;
import com.enonic.xp.index.PathIndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IndexConfigFactoryTest
{
    @Test
    void default_with_alias()
    {
        IndexConfigDocument minimal = create( "{\n" + " \"default\": \"minimal\"" + " }" );

        assertEquals( IndexConfig.MINIMAL, minimal.getConfigForPath( IndexPath.from( "my.random.path" ) ) );

        IndexConfigDocument byType = create( "{\n" + " \"default\": \"byType\"" + " }" );

        assertEquals( IndexConfig.BY_TYPE, byType.getConfigForPath( IndexPath.from( "my.random.path" ) ) );
    }

    @Test
    void default_full()
    {
        IndexConfigDocument config = create(
            " {" + "\"default\": {\n" + "                \"decideByType\": true,\n" + "                \"enabled\": true,\n" +
                "                \"nGram\": false,\n" + "                \"fulltext\": false,\n" +
                "                \"includeInAllText\": false,\n" + "                \"path\": true,\n" +
                "                \"indexValueProcessors\": [],\n" + "                \"languages\": []\n" + "            }" + "}" );

        assertTrue( config instanceof PatternIndexConfigDocument );
        final PatternIndexConfigDocument patternIndexConfigDocument = (PatternIndexConfigDocument) config;
        final IndexConfig defaultConfig = patternIndexConfigDocument.getDefaultConfig();
        assertEquals( true, defaultConfig.isDecideByType() );
        assertEquals( true, defaultConfig.isEnabled() );
        assertEquals( false, defaultConfig.isnGram() );
        assertEquals( false, defaultConfig.isFulltext() );
        assertEquals( false, defaultConfig.isIncludeInAllText() );
        assertEquals( true, defaultConfig.isPath() );
    }

    @Test
    void path_index_configs()
    {
        final PatternIndexConfigDocument fullConfig = createFullConfig();

        final SortedSet<PathIndexConfig> pathIndexConfigs = fullConfig.getPathIndexConfigs();

        assertEquals( 3, pathIndexConfigs.size() );
        assertEquals( IndexConfig.FULLTEXT, fullConfig.getConfigForPath( IndexPath.from( "displayName" ) ) );
    }

    @Test
    void indexProcessors()
    {
        final List<IndexValueProcessor> processors =
            createFullConfig().getConfigForPath( IndexPath.from( "myHtmlField" ) ).getIndexValueProcessors();
        assertEquals( 1, processors.size() );
        assertEquals( "htmlStripper", processors.get( 0 ).getName() );
    }

    @Test
    void languages()
    {
        final List<Locale> languages = createFullConfig().getConfigForPath( IndexPath.from( "myHtmlField" ) ).getLanguages();
        assertThat(languages).containsExactly( Locale.forLanguageTag("en"), Locale.forLanguageTag("no") );
    }

    @Test
    void empty()
    {
        IndexConfigDocument config = create( "{}" );
        assertEquals( IndexConfig.BY_TYPE, config.getConfigForPath( IndexPath.from( "my.random.path" ) ) );
    }

    @Test
    void unknown_alias()
    {
        assertThrows(IllegalArgumentException.class, () -> create( "{ \"default\" : \"fisk\" }" ));
    }

    @Test
    void allText_default()
    {
        IndexConfigDocument config = create( "{}" );
        assertTrue( config.getAllTextConfig().isEnabled() );
        assertTrue( config.getAllTextConfig().isnGram() );
        assertTrue( config.getAllTextConfig().isFulltext() );
        assertEquals( 0, config.getAllTextConfig().getLanguages().size() );
    }

    @Test
    void allText_custom()
    {
        IndexConfigDocument config = create(
            "{\n" +
                "  \"allText\": {\n" +
                "    \"enabled\": false,\n" +
                "    \"nGram\": false,\n" +
                "    \"fulltext\": true,\n" +
                "    \"languages\": [\"en\", \"no\"]\n" +
                "  }\n" +
                "}" );

        assertFalse( config.getAllTextConfig().isEnabled() );
        assertFalse( config.getAllTextConfig().isnGram() );
        assertTrue( config.getAllTextConfig().isFulltext() );
        assertThat( config.getAllTextConfig().getLanguages() ).containsExactly( Locale.forLanguageTag( "en" ), Locale.forLanguageTag( "no" ) );
    }

    @Test
    void allText_partial()
    {
        IndexConfigDocument config = create(
            "{\n" +
                "  \"allText\": {\n" +
                "    \"enabled\": false\n" +
                "  }\n" +
                "}" );

        assertFalse( config.getAllTextConfig().isEnabled() );
        assertTrue( config.getAllTextConfig().isnGram() ); // should be default
        assertTrue( config.getAllTextConfig().isFulltext() ); // should be default
        assertEquals( 0, config.getAllTextConfig().getLanguages().size() );
    }

    private IndexConfigDocument create( final String json )
    {
        final PropertyTree properties = PropertyTree.fromMap( JsonHelper.toMap( JsonHelper.from( json ) ) );

        return new IndexConfigFactory( properties.getRoot() ).create();
    }

    private PatternIndexConfigDocument createFullConfig()
    {
        final IndexConfigDocument indexConfigDoc = create(
            "{\n" + "            \"configs\": [\n" + "                {\n" + "                    \"path\": \"displayName\",\n" +
                "                    \"config\": \"fulltext\"" + "                },\n" + "                {\n" +
                "                    \"path\": \"myHtmlField\",\n" + "                    \"config\": {\n" +
                "                        \"decideByType\": false,\n" + "                        \"enabled\": true,\n" +
                "                        \"nGram\": false,\n" + "                        \"fulltext\": false,\n" +
                "                        \"includeInAllText\": true,\n" + "                        \"indexValueProcessors\": [\n" +
                "                            \"htmlStripper\"\n" + "                        ],\n" +
                "                        \"languages\": [\n" + "                            \"en\",\"no\"\n" +
                "                        ]\n" + "                    }\n" + "                },\n" + "                {\n" +
                "                    \"path\": \"type\",\n" + "                    \"config\": {\n" +
                "                        \"decideByType\": false,\n" + "                        \"enabled\": false,\n" +
                "                        \"nGram\": false,\n" + "                        \"fulltext\": false,\n" +
                "                        \"includeInAllText\": false,\n" + "                        \"path\": false,\n" +
                "                        \"indexValueProcessors\": []\n" + "                    }\n" + "                }\n" +
                "            ]\n" + "        }" );

        assertInstanceOf( PatternIndexConfigDocument.class, indexConfigDoc );
        return (PatternIndexConfigDocument) indexConfigDoc;
    }

}
