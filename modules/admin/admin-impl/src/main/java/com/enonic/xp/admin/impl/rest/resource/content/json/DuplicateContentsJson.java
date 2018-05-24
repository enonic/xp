package com.enonic.xp.admin.impl.rest.resource.content.json;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DuplicateContentsJson
{
    private List<DuplicateContentJson> contents;

    @JsonCreator
    public DuplicateContentsJson( @JsonProperty("aggregationQueries") final List<DuplicateContentJson> contents )
    {
        this.contents = contents;
    }

    public List<DuplicateContentJson> getContents()
    {
        return contents;
    }

    public void setContents( final List<DuplicateContentJson> contents )
    {
        this.contents = contents;
    }
}
