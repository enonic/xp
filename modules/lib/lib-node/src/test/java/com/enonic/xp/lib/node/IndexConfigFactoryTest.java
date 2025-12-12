package com.enonic.xp.lib.node;

import java.util.List;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        final List<String> languages = createFullConfig().getConfigForPath( IndexPath.from( "myHtmlField" ) ).getLanguages();
        assertEquals( 2, languages.size() );
        assertEquals( "en", languages.get( 0 ) );
        assertEquals( "no", languages.get( 1 ) );
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
        assertEquals( true, config.getAllTextConfig().isEnabled() );
        assertEquals( true, config.getAllTextConfig().isnGram() );
        assertEquals( true, config.getAllTextConfig().isFulltext() );
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

        assertEquals( false, config.getAllTextConfig().isEnabled() );
        assertEquals( false, config.getAllTextConfig().isnGram() );
        assertEquals( true, config.getAllTextConfig().isFulltext() );
        assertEquals( 2, config.getAllTextConfig().getLanguages().size() );
        assertEquals( "en", config.getAllTextConfig().getLanguages().get( 0 ) );
        assertEquals( "no", config.getAllTextConfig().getLanguages().get( 1 ) );
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

        assertEquals( false, config.getAllTextConfig().isEnabled() );
        assertEquals( true, config.getAllTextConfig().isnGram() ); // should be default
        assertEquals( true, config.getAllTextConfig().isFulltext() ); // should be default
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
