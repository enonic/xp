package com.enonic.wem.core.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.api.index.IndexConfig;

public class IndexConfigJson
{
    private boolean decideByType;

    private boolean enabled;

    private boolean nGram;

    private boolean fulltext;

    private boolean includeInAllText;

    @SuppressWarnings("UnusedDeclaration")
    @JsonCreator
    public IndexConfigJson( @JsonProperty("decideByType") final boolean decideByType, //
                            @JsonProperty("enabled") final boolean enabled, //
                            @JsonProperty("nGram") final boolean nGram, //
                            @JsonProperty("fulltext") final boolean fulltext, //
                            @JsonProperty("includeInAllText") final boolean includeInAllText )
    {
        this.decideByType = decideByType;
        this.enabled = enabled;
        this.nGram = nGram;
        this.fulltext = fulltext;
        this.includeInAllText = includeInAllText;
    }

    public IndexConfigJson( final IndexConfig indexConfig )
    {
        this.decideByType = indexConfig.isDecideByType();
        this.enabled = indexConfig.isEnabled();
        this.nGram = indexConfig.isnGram();
        this.fulltext = indexConfig.isFulltext();
        this.includeInAllText = indexConfig.isIncludeInAllText();
    }

    public IndexConfig toIndexConfig()
    {
        return IndexConfig.create().
            decideByType( this.decideByType ).
            enabled( this.enabled ).
            nGram( this.nGram ).
            fulltext( this.fulltext ).
            includeInAllText( this.includeInAllText ).
            build();
    }


    @SuppressWarnings("UnusedDeclaration")
    public boolean isDecideByType()
    {
        return decideByType;
    }

    @SuppressWarnings("UnusedDeclaration")
    public boolean isEnabled()
    {
        return enabled;
    }

    @SuppressWarnings("UnusedDeclaration")
    public boolean isnGram()
    {
        return nGram;
    }

    @SuppressWarnings("UnusedDeclaration")
    public boolean isFulltext()
    {
        return fulltext;
    }

    @SuppressWarnings("UnusedDeclaration")
    public boolean isIncludeInAllText()
    {
        return includeInAllText;
    }
}
