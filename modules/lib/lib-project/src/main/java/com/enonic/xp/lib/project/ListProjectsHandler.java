package com.enonic.xp.lib.project;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import com.enonic.xp.lib.project.command.GetProjectLanguageCommand;
import com.enonic.xp.lib.project.command.GetProjectReadAccessCommand;
import com.enonic.xp.lib.project.mapper.ProjectMapper;
import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.project.ProjectPermissions;
import com.enonic.xp.project.Projects;

public final class ListProjectsHandler
    extends BaseProjectHandler
{
    @Override
    protected List<ProjectMapper> doExecute()
    {
        final Projects projects = this.projectService.get().list();

        return projects.stream().
            map( project -> {
            final ProjectPermissions projectPermissions =
                !ProjectConstants.DEFAULT_PROJECT_NAME.equals( project.getName() ) ? this.projectService.get()
                    .getPermissions( project.getName() ) : null;

            final Boolean readAccess =
                !ProjectConstants.DEFAULT_PROJECT_NAME.equals( project.getName() ) ? GetProjectReadAccessCommand.create()
                    .contentService( this.contentService.get() )
                    .projectName( project.getName() )
                    .build()
                    .execute() : null;

            final Locale language = GetProjectLanguageCommand.create()
                .projectName( project.getName() )
                .contentService( this.contentService.get() )
                .build()
                .execute();

            return ProjectMapper.create()
                .setProject( project )
                .setLanguage( language )
                .setProjectPermissions( projectPermissions )
                .setIsPublic( readAccess )
                .build();

        } ).collect( Collectors.toList() );
    }
}
