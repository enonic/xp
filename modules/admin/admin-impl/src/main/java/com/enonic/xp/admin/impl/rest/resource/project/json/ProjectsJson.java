package com.enonic.xp.admin.impl.rest.resource.project.json;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;

import com.enonic.xp.project.Projects;

@Beta
public final class ProjectsJson
{
    private final ImmutableList<ProjectJson> projects;

    public ProjectsJson( final Projects projects )
    {
        this.projects = projects != null ? projects.stream().
            map( ProjectJson::new ).
            collect( ImmutableList.toImmutableList() ) : ImmutableList.of();
    }

    public ImmutableList<ProjectJson> getProjects()
    {
        return projects;
    }
}
