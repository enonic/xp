package com.enonic.xp.admin.impl.rest.resource.project.json;

import com.enonic.xp.project.ProjectName;

public final class DeleteProjectResultJson
{
    private final String name;

    public DeleteProjectResultJson( final ProjectName name )
    {
        this.name = name != null ? name.toString() : null;
    }

    public String getName()
    {
        return name;
    }
}
