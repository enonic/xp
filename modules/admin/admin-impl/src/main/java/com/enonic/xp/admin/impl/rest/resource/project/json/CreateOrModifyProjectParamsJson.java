package com.enonic.xp.admin.impl.rest.resource.project.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.project.ProjectName;

public final class CreateOrModifyProjectParamsJson
{
    private final ProjectName name;

    private final String displayName;

    private final String description;

    @JsonCreator
    CreateOrModifyProjectParamsJson( @JsonProperty("name") final String name, @JsonProperty("displayName") final String displayName,
                                     @JsonProperty("description") final String description )
    {
        this.name = ProjectName.from( name );
        this.displayName = displayName;
        this.description = description;
    }

    public ProjectName getName()
    {
        return name;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getDescription()
    {
        return description;
    }
}
