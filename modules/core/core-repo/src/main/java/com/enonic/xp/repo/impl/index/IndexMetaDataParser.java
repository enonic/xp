package com.enonic.xp.repo.impl.index;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.collect.Maps;

public class IndexMetaDataParser
{
    private static final String STEMMED_LANGUAGE_NAME_PREFIX = "([a-zA-Z_]+)";

    private static final String STEMMED_TEMPLATE_NAME_PREFIX = "template" + StemmedIndexValueType.STEMMED_INDEX_PREFIX;

    private static final String STEMMED_ANALYZER_NAME_PREFIX = "language_analyzer_";

    private static final Pattern STEMMED_TEMPLATE_NAME_PATTERN =
        Pattern.compile( "^" + STEMMED_TEMPLATE_NAME_PREFIX + STEMMED_LANGUAGE_NAME_PREFIX + "$" );

    private static final Pattern STEMMED_ANALYZER_NAME_PATTERN =
        Pattern.compile( "^" + STEMMED_ANALYZER_NAME_PREFIX + STEMMED_LANGUAGE_NAME_PREFIX + "$" );

    private static final Pattern STEMMED_TEMPLATE_MATCH_PATTERN =
        Pattern.compile( "^*\\." + "(" + StemmedIndexValueType.STEMMED_INDEX_PREFIX + STEMMED_LANGUAGE_NAME_PREFIX + ")" + "$" );

    private Map<String, Object> indexMetaData;

    private List<Map> stemmedTemplates;

    public IndexMetaDataParser( final Map<String, Object> indexMetaData )
    {
        this.indexMetaData = indexMetaData;
    }

    public IndexMetaDataParser parse()
    {
        final List<Map<String, Map>> dynamicTemplates = getDynamicTemplates();
        this.stemmedTemplates = getStemmedTemplates( dynamicTemplates );

        return this;
    }

    public Map<String, String> getStemmedAnalyzers()
    {
        return parseMatchedAnalyzers();
    }

    public Map<String, StemmedIndexValueType> getStemmedIndexValueTypes()
    {
        return parseMatchedIndexValueTypes();
    }

    private List<Map<String, Map>> getDynamicTemplates()
    {
        return (List<Map<String, Map>>) indexMetaData.get( "dynamic_templates" );
    }

    private List<Map> getStemmedTemplates( final List<Map<String, Map>> dynamicTemplates )
    {
        return dynamicTemplates.stream().
            filter( dynamicTemplate -> dynamicTemplate.keySet().stream().
                anyMatch( templateName -> STEMMED_TEMPLATE_NAME_PATTERN.matcher( templateName ).
                    find() ) ).
            map( dynamicTemplate -> dynamicTemplate.values().
                stream().
                findFirst().
                get() ).
            collect( Collectors.toList() );
    }

    private Map<String, String> parseMatchedAnalyzers()
    {
        final Map<String, String> result = Maps.newHashMap();

        this.stemmedTemplates.stream().
            map( stemmedTemplate -> (Map) stemmedTemplate.get( "mapping" ) ).
            map( stemmedTemplate -> (String) stemmedTemplate.get( "analyzer" ) ).
            forEach( matchString -> {
                final Matcher matcher = STEMMED_ANALYZER_NAME_PATTERN.matcher( matchString );

                if ( matcher.find() )
                {
                    result.put( matcher.group( 1 ), matcher.group( 0 ) );
                }
            } );

        return result;
    }

    private Map<String, StemmedIndexValueType> parseMatchedIndexValueTypes()
    {

        final Map<String, StemmedIndexValueType> result = Maps.newHashMap();

        this.stemmedTemplates.stream().
            map( stemmedTemplate -> (String) stemmedTemplate.get( "match" ) ).
            forEach( matchString -> {
                final Matcher matcher = STEMMED_TEMPLATE_MATCH_PATTERN.matcher( matchString );

                if ( matcher.find() )
                {
                    result.put( matcher.group( 2 ), new StemmedIndexValueType( matcher.group( 1 ) ) );
                }
            } );

        return result;
    }


}
