package com.enonic.xp.repo.impl.node.json;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexValueProcessor;
import com.enonic.xp.index.IndexValueProcessors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class IndexConfigJson
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

    @JsonProperty("indexValueProcessors")
    private List<String> indexValueProcessors;

    @JsonProperty("languages")
    private List<String> languages;

    public static IndexConfigJson toJson( final IndexConfig config )
    {
        final IndexConfigJson json = new IndexConfigJson();
        json.decideByType = config.isDecideByType();
        json.enabled = config.isEnabled();
        json.nGram = config.isnGram();
        json.fulltext = config.isFulltext();
        json.includeInAllText = config.isIncludeInAllText();
        if ( !config.getIndexValueProcessors().isEmpty() )
        {
            json.indexValueProcessors = toStringList( config.getIndexValueProcessors() );
        }
        if ( !config.getLanguages().isEmpty() )
        {
            json.languages = new ArrayList<>( config.getLanguages() );
        }
        return json;
    }

    public IndexConfig fromJson()
    {
        final IndexConfig.Builder builder = IndexConfig.create().
            decideByType( this.decideByType ).
            enabled( this.enabled ).
            nGram( this.nGram ).
            fulltext( this.fulltext ).
            includeInAllText( this.includeInAllText );

        if ( this.indexValueProcessors != null )
        {
            for ( final String indexValueProcessor : this.indexValueProcessors )
            {
                builder.addIndexValueProcessor( IndexValueProcessors.get( indexValueProcessor ) );
            }
        }

        if ( this.languages != null )
        {
            for ( final String language : this.languages )
            {
                builder.addLanguage( language );
            }
        }

        return builder.build();
    }

    private static List<String> toStringList( final Iterable<IndexValueProcessor> indexValueProcessors )
    {
        return stream( indexValueProcessors.spliterator(), false ).
            map( IndexValueProcessor::getName ).
            collect( toList() );
    }
}
