package com.enonic.wem.core.search.elastic;

import org.elasticsearch.common.settings.Settings;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

import com.enonic.wem.core.config.ConfigProperties;

import static org.junit.Assert.*;

public class IndexSettingsBuilderTest
{
    @Test
    public void testBuildIndexSettings()
        throws Exception
    {

        IndexSettingsSourceProvider indexSettingsSourceProvider = Mockito.mock( IndexSettingsSourceProvider.class );

        String source = "{\n" +
            "    \"analysis\" : {\n" +
            "        \"analyzer\" : {\n" +
            "            \"keywordlowercase\" : {\n" +
            "                \"type\" : \"custom\",\n" +
            "                \"tokenizer\" : \"keyword\",\n" +
            "                \"filter\" : [\"lowercase\"]\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "}";

        Mockito.when( indexSettingsSourceProvider.getSources() ).thenReturn( Lists.newArrayList( source ) );

        IndexSettingsBuilder indexSettingsBuilder = new IndexSettingsBuilder();
        indexSettingsBuilder.setIndexSettingsSourceProvider( indexSettingsSourceProvider );

        ConfigProperties configProperties = new ConfigProperties();
        configProperties.put( "cms.elasticsearch.index.myProperty", "myPropertyValue" );
        configProperties.put( "cms.elasticsearch.myNotIndexProperty", "myNotIndexPropertyValue" );
        indexSettingsBuilder.setConfigProperties( configProperties );

        final Settings settings = indexSettingsBuilder.buildIndexSettings();

        assertEquals( 4, settings.getAsMap().size() );

        assertEquals( "myPropertyValue", settings.get( "index.myProperty" ) );
        assertTrue( settings.get( "myNotIndexProperty" ) == null );

    }
}
