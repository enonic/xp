package com.enonic.wem.repo.internal.entity.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.index.PathIndexConfig;

final class PatternConfigJson
{
    @JsonProperty("path")
    private String path;

    @JsonProperty("indexConfig")
    private IndexConfigJson indexConfig;

    public PathIndexConfig fromJson()
    {
        return PathIndexConfig.create().
            path( PropertyPath.from( this.path ) ).
            indexConfig( this.indexConfig.fromJson() ).
            build();
    }

    public static PatternConfigJson toJson( final PathIndexConfig config )
    {
        final PatternConfigJson json = new PatternConfigJson();
        json.path = config.getPath().toString();
        json.indexConfig = IndexConfigJson.toJson( config.getIndexConfig() );
        return json;
    }
}
