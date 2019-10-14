package com.enonic.xp.admin.impl.rest.resource.project.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.annotations.Beta;

import com.enonic.xp.project.ProjectName;

@Beta
public final class DeleteProjectParamsJson
{
    private final ProjectName name;

    @JsonCreator
    DeleteProjectParamsJson( @JsonProperty("name") final String name )
    {
        this.name = ProjectName.from( name );
    }

    public ProjectName getName()
    {
        return name;
    }
}
