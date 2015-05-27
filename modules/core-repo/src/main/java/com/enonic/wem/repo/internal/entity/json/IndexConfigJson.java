package com.enonic.wem.repo.internal.entity.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexValueProcessor;
import com.enonic.xp.index.IndexValueProcessorRegistry;

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

@JsonInclude(JsonInclude.Include.NON_NULL)
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

    @JsonProperty("indexValueProcessors")
    private List<String> indexValueProcessors;

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
                builder.addIndexValueProcessor( IndexValueProcessorRegistry.getIndexValueProcessor( indexValueProcessor ) );
            }
        }

        return builder.build();
    }

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
        return json;
    }

    private static List<String> toStringList( final Iterable<IndexValueProcessor> indexValueProcessors )
    {
        return stream( indexValueProcessors.spliterator(), false ).
            map( IndexValueProcessor::toString ).
            collect( toList() );
    }
}
