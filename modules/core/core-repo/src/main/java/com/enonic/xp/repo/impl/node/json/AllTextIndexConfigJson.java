package com.enonic.xp.repo.impl.node.json;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.index.AllTextIndexConfig;

@JsonInclude(JsonInclude.Include.NON_NULL)
final class AllTextIndexConfigJson
{
    @JsonProperty("languages")
    private List<String> languages = new ArrayList<>();

    @JsonProperty("enabled")
    private Boolean enabled;

    @JsonProperty("nGram")
    private Boolean nGram;

    @JsonProperty("fulltext")
    private Boolean fulltext;

    public static AllTextIndexConfigJson toJson( final AllTextIndexConfig config )
    {
        final AllTextIndexConfigJson json = new AllTextIndexConfigJson();
        if ( !config.getLanguages().isEmpty() )
        {
            json.languages = new ArrayList<>( config.getLanguages() );
        }
        // Only serialize if different from defaults
        if ( !config.isEnabled() )
        {
            json.enabled = config.isEnabled();
        }
        if ( !config.isnGram() )
        {
            json.nGram = config.isnGram();
        }
        if ( !config.isFulltext() )
        {
            json.fulltext = config.isFulltext();
        }
        return json;
    }

    public AllTextIndexConfig fromJson()
    {
        final AllTextIndexConfig.Builder builder = AllTextIndexConfig.create();

        if ( this.languages != null )
        {
            for ( final String language : this.languages )
            {
                builder.addLanguage( language );
            }
        }

        if ( this.enabled != null )
        {
            builder.enabled( this.enabled );
        }

        if ( this.nGram != null )
        {
            builder.nGram( this.nGram );
        }

        if ( this.fulltext != null )
        {
            builder.fulltext( this.fulltext );
        }

        return builder.build();
    }
}
