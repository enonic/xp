package com.enonic.xp.admin.impl.rest.resource.project.json;

import com.google.common.base.Preconditions;

import com.enonic.xp.project.ProjectGraphEntry;

public final class ProjectGraphEntryJson
{
    private final String name;

    private final String parent;

    public ProjectGraphEntryJson( final ProjectGraphEntry project )
    {
        Preconditions.checkArgument( project != null, "projectGraphEntry cannot be null." );
        Preconditions.checkArgument( project.getName() != null, "Project name cannot be null." );

        this.name = project.getName().toString();
        this.parent = project.getParent() != null ? project.getParent().toString() : null;
    }

    public String getName()
    {
        return name;
    }

    public String getParent()
    {
        return parent;
    }
}
