package com.enonic.wem.core.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.api.entity.PathIndexConfig;

public class PathIndexConfigJson
{
    private final PropertyIndexConfigJson propertyIndexConfigJson;

    private final String dataPath;

    public PathIndexConfigJson( final PathIndexConfig pathIndexConfig )
    {
        this.propertyIndexConfigJson = new PropertyIndexConfigJson( pathIndexConfig.getPropertyIndexConfig() );
        this.dataPath = pathIndexConfig.getPath().toString();
    }


    @SuppressWarnings("UnusedDeclaration")
    @JsonCreator
    public PathIndexConfigJson( @JsonProperty("propertyIndexConfig") final PropertyIndexConfigJson propertyIndexConfigJson,
                                @JsonProperty("dataPath") final String dataPath )

    {
        this.propertyIndexConfigJson = propertyIndexConfigJson;
        this.dataPath = dataPath;
    }

    public PropertyIndexConfigJson getPropertyIndexConfigJson()
    {
        return propertyIndexConfigJson;
    }

    public String getDataPath()
    {
        return dataPath;
    }
}
