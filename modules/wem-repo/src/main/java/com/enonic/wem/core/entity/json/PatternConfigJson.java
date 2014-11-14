package com.enonic.wem.core.entity.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.api.data.DataPath;
import com.enonic.wem.api.index.PathIndexConfig;

final class PatternConfigJson
{
    @JsonProperty("path")
    private String path;

    @JsonProperty("indexConfig")
    private IndexConfigJson indexConfig;

    public PathIndexConfig fromJson()
    {
        return PathIndexConfig.create().
            path( DataPath.from( this.path ) ).
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
