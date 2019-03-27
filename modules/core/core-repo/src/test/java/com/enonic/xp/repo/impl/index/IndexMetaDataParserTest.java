package com.enonic.xp.repo.impl.index;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;

import com.enonic.xp.json.ObjectMapperHelper;

import static org.junit.Assert.*;

public class IndexMetaDataParserTest
{
    @Test
    public void test_parse_templates()
        throws Exception
    {
        URL url = getClass().getResource( "stemmed_templates.json" );
        final HashMap templatesAsMap = ObjectMapperHelper.create().readValue( url, HashMap.class );

        final Map<String, Map> dynamicTemplatesMap = ( (HashMap) ( (ArrayList) templatesAsMap.get( "dynamic_templates" ) ).get( 0 ) );
        final List templatesList =

            dynamicTemplatesMap.entrySet().stream().map( entry -> {
                HashMap map = new HashMap<>();
                map.put( entry.getKey(), entry.getValue() );

                return map;

            } ).collect( Collectors.toList() );

        final HashMap normalizedMap = new HashMap();
        normalizedMap.put( "dynamic_templates", templatesList );

        final IndexMetaDataParser parser = new IndexMetaDataParser( normalizedMap );

        parser.parse();

        final Map<String, String> analyzers = parser.getStemmedAnalyzers();
        assertEquals( 2, analyzers.size() );

        assertEquals( "language_analyzer_en", analyzers.get( "en" ) );
        assertEquals( "language_analyzer_no", analyzers.get( "no" ) );

        final Map<String, StemmedIndexValueType> indexValueTypes = parser.getStemmedIndexValueTypes();
        assertEquals( 2, indexValueTypes.size() );

        assertEquals( new StemmedIndexValueType( "en" ), indexValueTypes.get( "en" ) );
        assertEquals( new StemmedIndexValueType( "no" ), indexValueTypes.get( "no" ) );
    }
}