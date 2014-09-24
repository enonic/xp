package com.enonic.wem.core.entity;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Sets;

import com.enonic.wem.api.index.PatternBasedIndexConfigDocument;
import com.enonic.wem.api.index.PatternConfig;
import com.enonic.wem.core.entity.relationship.IndexConfigDocumentJson;

public class PatternBasedIndexConfigDocumentJson
    extends IndexConfigDocumentJson
{
    public String analyzer;

    public Set<PatternConfigJson> patternConfigs = Sets.newHashSet();

    public IndexConfigJson defaultConfig;

    @SuppressWarnings("UnusedDeclaration")
    @JsonCreator
    public PatternBasedIndexConfigDocumentJson( @JsonProperty("analyzer") final String analyzer, //
                                                @JsonProperty("defaultConfig") IndexConfigJson defaultConfig,  //
                                                @JsonProperty("patternConfigs") Set<PatternConfigJson> patternConfigs )
    {
        this.analyzer = analyzer;
        this.patternConfigs = patternConfigs;
        this.defaultConfig = defaultConfig;
    }

    public PatternBasedIndexConfigDocumentJson( final PatternBasedIndexConfigDocument indexConfigDocument )
    {
        this.analyzer = indexConfigDocument.getAnalyzer();

        for ( final PatternConfig patternConfig : indexConfigDocument.patternConfigs )
        {
            patternConfigs.add( new PatternConfigJson( patternConfig ) );
        }

        this.defaultConfig = new IndexConfigJson( indexConfigDocument.getDefaultConfig() );
    }

    public PatternBasedIndexConfigDocument toEntityIndexConfig()
    {
        final PatternBasedIndexConfigDocument.Builder builder = PatternBasedIndexConfigDocument.create().
            analyzer( this.analyzer ).
            defaultConfig( this.defaultConfig.toIndexConfig() );

        for ( final PatternConfigJson patternConfigJson : this.patternConfigs )
        {
            builder.addPattern( patternConfigJson.toPatternConfig() );
        }

        return builder.build();
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getAnalyzer()
    {
        return analyzer;
    }

    @SuppressWarnings("UnusedDeclaration")
    public Set<PatternConfigJson> getPatternConfigs()
    {
        return patternConfigs;
    }

    @SuppressWarnings("UnusedDeclaration")
    public IndexConfigJson getDefaultConfig()
    {
        return defaultConfig;
    }
}
