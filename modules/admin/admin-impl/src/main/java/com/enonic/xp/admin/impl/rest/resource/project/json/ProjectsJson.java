package com.enonic.xp.admin.impl.rest.resource.project.json;

import java.util.List;

import com.google.common.collect.ImmutableList;

public final class ProjectsJson
{
    private final ImmutableList<ProjectJson> projects;

    public ProjectsJson( final List<ProjectJson> projects )
    {
        this.projects = ImmutableList.copyOf( projects );
    }

    public ImmutableList<ProjectJson> getProjects()
    {
        return projects;
    }
}
