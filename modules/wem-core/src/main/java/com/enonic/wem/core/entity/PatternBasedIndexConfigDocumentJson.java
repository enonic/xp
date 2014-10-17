package com.enonic.wem.core.entity;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Sets;

import com.enonic.wem.api.index.PathIndexConfig;
import com.enonic.wem.api.index.PatternIndexConfigDocument;

public class PatternBasedIndexConfigDocumentJson
    extends IndexConfigDocumentJson
{
    private final String analyzer;

    private Set<PatternConfigJson> patternConfigs = Sets.newHashSet();

    private final IndexConfigJson defaultConfig;

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

    public PatternBasedIndexConfigDocumentJson( final PatternIndexConfigDocument indexConfigDocument )
    {
        this.analyzer = indexConfigDocument.getAnalyzer();

        for ( final PathIndexConfig pathIndexConfig : indexConfigDocument.pathIndexConfigs )
        {
            patternConfigs.add( new PatternConfigJson( pathIndexConfig ) );
        }

        this.defaultConfig = new IndexConfigJson( indexConfigDocument.getDefaultConfig() );
    }

    public PatternIndexConfigDocument toEntityIndexConfig()
    {
        final PatternIndexConfigDocument.Builder builder = PatternIndexConfigDocument.create().
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
