package com.enonic.wem.core.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.api.data.DataPath;
import com.enonic.wem.api.index.PatternConfig;

public class PatternConfigJson
{
    private String path;

    private IndexConfigJson indexConfig;

    @SuppressWarnings("UnusedDeclaration")
    @JsonCreator
    public PatternConfigJson( @JsonProperty("path") final String path, //
                              @JsonProperty("indexConfig") final IndexConfigJson indexConfig )
    {
        this.path = path;
        this.indexConfig = indexConfig;
    }

    public PatternConfigJson( final PatternConfig patternConfig )
    {
        this.path = patternConfig.getPath().toString();
        this.indexConfig = new IndexConfigJson( patternConfig.getIndexConfig() );
    }

    public PatternConfig toPatternConfig()
    {
        return PatternConfig.create().
            path( DataPath.from( this.path ) ).
            indexConfig( this.indexConfig.toIndexConfig() ).
            build();
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getPath()
    {
        return path;
    }

    @SuppressWarnings("UnusedDeclaration")
    public IndexConfigJson getIndexConfig()
    {
        return indexConfig;
    }

}
