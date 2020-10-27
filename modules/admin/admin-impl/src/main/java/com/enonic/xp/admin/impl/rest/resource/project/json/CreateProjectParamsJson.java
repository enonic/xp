package com.enonic.xp.admin.impl.rest.resource.project.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.project.ProjectName;

public final class CreateProjectParamsJson
{
    private final ProjectName name;

    private final String displayName;

    private final String description;

    private final ProjectName parent;

    @JsonCreator
    CreateProjectParamsJson( @JsonProperty("name") final String name, @JsonProperty("displayName") final String displayName,
                             @JsonProperty("description") final String description, @JsonProperty("parent") final String parent )
    {
        this.name = ProjectName.from( name );
        this.displayName = displayName;
        this.description = description;
        this.parent = parent == null || parent.isBlank() ? null : ProjectName.from( parent );
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

    public ProjectName getParent()
    {
        return parent;
    }
}
