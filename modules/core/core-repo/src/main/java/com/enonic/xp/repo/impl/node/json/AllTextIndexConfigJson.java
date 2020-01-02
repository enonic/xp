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

    public static AllTextIndexConfigJson toJson( final AllTextIndexConfig config )
    {
        final AllTextIndexConfigJson json = new AllTextIndexConfigJson();
        if ( !config.getLanguages().isEmpty() )
        {
            json.languages = new ArrayList<>( config.getLanguages() );
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

        return builder.build();
    }
}
