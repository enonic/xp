package com.enonic.wem.repo.internal.entity.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.core.index.IndexConfig;

final class IndexConfigJson
{
    @JsonProperty("decideByType")
    private boolean decideByType;

    @JsonProperty("enabled")
    private boolean enabled;

    @JsonProperty("nGram")
    private boolean nGram;

    @JsonProperty("fulltext")
    private boolean fulltext;

    @JsonProperty("includeInAllText")
    private boolean includeInAllText;

    public IndexConfig fromJson()
    {
        return IndexConfig.create().
            decideByType( this.decideByType ).
            enabled( this.enabled ).
            nGram( this.nGram ).
            fulltext( this.fulltext ).
            includeInAllText( this.includeInAllText ).
            build();
    }

    public static IndexConfigJson toJson( final IndexConfig config )
    {
        final IndexConfigJson json = new IndexConfigJson();
        json.decideByType = config.isDecideByType();
        json.enabled = config.isEnabled();
        json.nGram = config.isnGram();
        json.fulltext = config.isFulltext();
        json.includeInAllText = config.isIncludeInAllText();
        return json;
    }
}
