package com.enonic.xp.lib.node;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableSortedSet;

import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.index.PathIndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.json.JsonToPropertyTreeTranslator2;
import com.enonic.xp.util.JsonHelper;

import static org.junit.Assert.*;

public class IndexConfigFactoryTest
{
    @Test
    public void default_with_alias()
        throws Exception
    {
        IndexConfigDocument minimal = create( "{\n" +
                                                  " \"default\": \"minimal\"" +
                                                  " }" );

        assertEquals( IndexConfig.MINIMAL, minimal.getConfigForPath( PropertyPath.from( "my.random.path" ) ) );

        IndexConfigDocument byType = create( "{\n" +
                                                 " \"default\": \"byType\"" +
                                                 " }" );

        assertEquals( IndexConfig.BY_TYPE, byType.getConfigForPath( PropertyPath.from( "my.random.path" ) ) );
    }

    @Test
    public void default_full()
        throws Exception
    {
        IndexConfigDocument config = create( " {" +
                                                 "\"default\": {\n" +
                                                 "                \"decideByType\": true,\n" +
                                                 "                \"enabled\": true,\n" +
                                                 "                \"nGram\": false,\n" +
                                                 "                \"fulltext\": false,\n" +
                                                 "                \"includeInAllText\": false,\n" +
                                                 "                \"indexValueProcessors\": []\n" +
                                                 "            }" +
                                                 "}" );

        assertTrue( config instanceof PatternIndexConfigDocument );
        final PatternIndexConfigDocument patternIndexConfigDocument = (PatternIndexConfigDocument) config;
        final IndexConfig defaultConfig = patternIndexConfigDocument.getDefaultConfig();
        assertEquals( true, defaultConfig.isDecideByType() );
        assertEquals( true, defaultConfig.isEnabled() );
        assertEquals( false, defaultConfig.isnGram() );
        assertEquals( false, defaultConfig.isFulltext() );
        assertEquals( false, defaultConfig.isIncludeInAllText() );
    }

    @Test
    public void path_index_configs()
        throws Exception
    {
        final PatternIndexConfigDocument fullConfig = createFullConfig();

        final ImmutableSortedSet<PathIndexConfig> pathIndexConfigs = fullConfig.getPathIndexConfigs();

        assertEquals( 3, pathIndexConfigs.size() );
        assertEquals( IndexConfig.FULLTEXT, fullConfig.getConfigForPath( PropertyPath.from( "displayName" ) ) );
    }


    @Test
    public void empty()
        throws Exception
    {
        IndexConfigDocument config = create( "{}" );
        assertEquals( IndexConfig.BY_TYPE, config.getConfigForPath( PropertyPath.from( "my.random.path" ) ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void unknown_alias()
        throws Exception
    {
        create( "{ \"default\" : \"fisk\" }" );
    }

    private IndexConfigDocument create( final String json )
    {
        final JsonNode node = JsonHelper.from( json );

        final PropertyTree properties = JsonToPropertyTreeTranslator2.translate( node );

        return new IndexConfigFactory( properties.getRoot() ).create();
    }

    private PatternIndexConfigDocument createFullConfig()
    {
        final IndexConfigDocument indexConfigDoc = create( "{\n" +
                                                               "            \"configs\": [\n" +
                                                               "                {\n" +
                                                               "                    \"path\": \"displayName\",\n" +
                                                               "                    \"config\": \"fulltext\"" +
                                                               "                },\n" +
                                                               "                {\n" +
                                                               "                    \"path\": \"myHtmlField\",\n" +
                                                               "                    \"config\": {\n" +
                                                               "                        \"decideByType\": false,\n" +
                                                               "                        \"enabled\": true,\n" +
                                                               "                        \"nGram\": false,\n" +
                                                               "                        \"fulltext\": false,\n" +
                                                               "                        \"includeInAllText\": true,\n" +
                                                               "                        \"indexValueProcessors\": [\n" +
                                                               "                            \"myProcessor\"\n" +
                                                               "                        ]\n" +
                                                               "                    }\n" +
                                                               "                },\n" +
                                                               "                {\n" +
                                                               "                    \"path\": \"type\",\n" +
                                                               "                    \"config\": {\n" +
                                                               "                        \"decideByType\": false,\n" +
                                                               "                        \"enabled\": false,\n" +
                                                               "                        \"nGram\": false,\n" +
                                                               "                        \"fulltext\": false,\n" +
                                                               "                        \"includeInAllText\": false,\n" +
                                                               "                        \"indexValueProcessors\": []\n" +
                                                               "                    }\n" +
                                                               "                }\n" +
                                                               "            ]\n" +
                                                               "        }" );

        assertTrue( indexConfigDoc instanceof PatternIndexConfigDocument );
        return (PatternIndexConfigDocument) indexConfigDoc;
    }

}