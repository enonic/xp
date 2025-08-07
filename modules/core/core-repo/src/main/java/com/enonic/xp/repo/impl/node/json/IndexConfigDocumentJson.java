package com.enonic.xp.repo.impl.node.json;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.index.PatternIndexConfigDocument;

public final class IndexConfigDocumentJson
{
    @JsonProperty("analyzer")
    private String analyzer;

    @JsonProperty("patternConfigs")
    private List<PatternConfigJson> patternConfigs;

    @JsonProperty("defaultConfig")
    private IndexConfigJson defaultConfig;

    @JsonProperty("allTextConfig")
    private AllTextIndexConfigJson allTextConfig;

    public static IndexConfigDocumentJson toJson( final PatternIndexConfigDocument config )
    {
        final IndexConfigDocumentJson json = new IndexConfigDocumentJson();
        json.analyzer = config.getAnalyzer();
        json.patternConfigs = config.getPathIndexConfigs().stream().map( PatternConfigJson::toJson ).collect( Collectors.toList() );
        json.defaultConfig = IndexConfigJson.toJson( config.getDefaultConfig() );
        json.allTextConfig = AllTextIndexConfigJson.toJson( config.getAllTextConfig() );
        return json;
    }

    public static PatternIndexConfigDocument fromJson( final IndexConfigDocumentJson json )
    {
        final PatternIndexConfigDocument.Builder builder = PatternIndexConfigDocument.create().
            analyzer( json.analyzer ).
            defaultConfig( json.defaultConfig.fromJson() );

        for ( final PatternConfigJson patternConfigJson : json.patternConfigs )
        {
            builder.add( patternConfigJson.fromJson() );
        }

        if ( json.allTextConfig != null )
        {
            json.allTextConfig.fromJson().getLanguages().forEach( builder::addAllTextConfigLanguage );
        }
        return builder.build();
    }
}
