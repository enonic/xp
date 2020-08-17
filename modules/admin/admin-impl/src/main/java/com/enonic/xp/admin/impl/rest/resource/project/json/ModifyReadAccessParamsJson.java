package com.enonic.xp.admin.impl.rest.resource.project.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.admin.impl.rest.resource.project.ProjectReadAccess;
import com.enonic.xp.project.ProjectName;

public final class ModifyReadAccessParamsJson
{
    private final ProjectName name;

    private final ProjectReadAccess readAccess;

    @JsonCreator
    ModifyReadAccessParamsJson( @JsonProperty("name") final String name,
                                @JsonProperty("readAccess") final ProjectReadAccessJson readAccess )
    {
        this.readAccess = readAccess.getProjectReadAccess();
        this.name = ProjectName.from( name );
    }

    public ProjectReadAccess getReadAccess()
    {
        return readAccess;
    }

    public ProjectName getName()
    {
        return name;
    }
}
