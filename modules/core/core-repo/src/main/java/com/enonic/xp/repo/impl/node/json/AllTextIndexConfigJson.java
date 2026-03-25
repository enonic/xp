package com.enonic.xp.repo.impl.node.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.index.AllTextIndexConfig;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class AllTextIndexConfigJson
{
    @JsonProperty("languages")
    public List<String> languages = new ArrayList<>();

    @JsonProperty("enabled")
    public Boolean enabled;

    @JsonProperty("nGram")
    public Boolean nGram;

    @JsonProperty("fulltext")
    public Boolean fulltext;

    public static AllTextIndexConfigJson toJson( final AllTextIndexConfig config )
    {
        final AllTextIndexConfigJson json = new AllTextIndexConfigJson();
        if ( !config.getLanguages().isEmpty() )
        {
            json.languages = config.getLanguages().stream().map( Locale::toLanguageTag ).toList();
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
            this.languages.stream().map( Locale::forLanguageTag ).forEach( builder::addLanguage );
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
