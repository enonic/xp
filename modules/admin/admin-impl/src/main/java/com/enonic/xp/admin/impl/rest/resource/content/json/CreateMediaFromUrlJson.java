package com.enonic.xp.admin.impl.rest.resource.content.json;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class CreateMediaFromUrlJson
{
    private final String parent;
    private final String name;
    private final String url;

    @JsonCreator
    CreateMediaFromUrlJson( @JsonProperty("parent") final String parent, @JsonProperty("name") final String name, @JsonProperty("url") final String url )
    {
       this.parent = parent;
       this.name = name;
       this.url = url;
    }

    public String getParent()
    {
        return parent;
    }

    public String getName()
    {
        return name;
    }

    public String getUrl()
    {
        return url;
    }
}
