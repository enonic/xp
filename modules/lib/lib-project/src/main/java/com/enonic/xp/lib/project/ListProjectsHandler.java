package com.enonic.xp.lib.project;

import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.lib.project.mapper.ProjectMapper;
import com.enonic.xp.project.ProjectPermissions;
import com.enonic.xp.project.Projects;

public final class ListProjectsHandler
    extends BaseProjectHandler
{
    @Override
    protected List<ProjectMapper> doExecute()
    {
        final Projects projects = this.projectService.get().list();

        return projects.stream().map( project -> {
            final ProjectPermissions projectPermissions = this.projectService.get().getPermissions( project.getName() );

            final boolean publicRead = this.projectService.get().getPublicRead( project.getName() );

            return ProjectMapper.create()
                .setProject( project )
                .setProjectPermissions( projectPermissions )
                .setPublicRead( publicRead )
                .build();

        } ).collect( Collectors.toList() );
    }
}
