package com.enonic.xp.repo.impl.node.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.index.IndexPath;
import com.enonic.xp.index.PathIndexConfig;

public final class PatternConfigJson
{
    @JsonProperty("path")
    private String path;

    @JsonProperty("indexConfig")
    private IndexConfigJson indexConfig;

    public static PatternConfigJson toJson( final PathIndexConfig config )
    {
        final PatternConfigJson json = new PatternConfigJson();
        json.path = config.getIndexPath().toString();
        json.indexConfig = IndexConfigJson.toJson( config.getIndexConfig() );
        return json;
    }

    public PathIndexConfig fromJson()
    {
        return PathIndexConfig.create().path( IndexPath.from( this.path ) ).
            indexConfig( this.indexConfig.fromJson() ).
            build();
    }
}
