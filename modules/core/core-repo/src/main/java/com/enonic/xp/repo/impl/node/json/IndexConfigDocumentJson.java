package com.enonic.xp.repo.impl.node.json;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.index.PatternIndexConfigDocument;

final class IndexConfigDocumentJson
{
    @JsonProperty("analyzer")
    private String analyzer;

    @JsonProperty("patternConfigs")
    private List<PatternConfigJson> patternConfigs;

    @JsonProperty("defaultConfig")
    private IndexConfigJson defaultConfig;

    public PatternIndexConfigDocument fromJson()
    {
        final PatternIndexConfigDocument.Builder builder = PatternIndexConfigDocument.create().
            analyzer( this.analyzer ).
            defaultConfig( this.defaultConfig.fromJson() );

        for ( final PatternConfigJson patternConfigJson : this.patternConfigs )
        {
            builder.addPattern( patternConfigJson.fromJson() );
        }

        return builder.build();
    }

    public static IndexConfigDocumentJson toJson( final PatternIndexConfigDocument config )
    {
        final IndexConfigDocumentJson json = new IndexConfigDocumentJson();
        json.analyzer = config.getAnalyzer();
        json.patternConfigs = config.getPathIndexConfigs().stream().map( PatternConfigJson::toJson ).collect( Collectors.toList() );
        json.defaultConfig = IndexConfigJson.toJson( config.getDefaultConfig() );
        return json;
    }
}
