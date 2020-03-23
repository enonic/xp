package com.enonic.xp.admin.impl.rest.resource.project.json;

import java.util.Map;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectPermissions;

public final class ProjectsJson
{
    private final ImmutableList<ProjectJson> projects;

    public ProjectsJson( final Map<Project, ProjectPermissions> projects )
    {
        this.projects = projects != null ? projects.entrySet().stream().
            map( entry -> new ProjectJson( entry.getKey(), entry.getValue() ) ).
            collect( ImmutableList.toImmutableList() ) : ImmutableList.of();
    }

    public ImmutableList<ProjectJson> getProjects()
    {
        return projects;
    }
}
