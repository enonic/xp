package com.enonic.xp.lib.project;

import java.util.Locale;
import java.util.Objects;

import com.enonic.xp.lib.project.command.GetProjectLanguageCommand;
import com.enonic.xp.lib.project.command.GetProjectReadAccessCommand;
import com.enonic.xp.lib.project.mapper.ProjectMapper;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectPermissions;

public final class GetProjectHandler
    extends BaseProjectHandler
{
    private ProjectName id;

    @Override
    protected ProjectMapper doExecute()
    {
        final Project project = this.projectService.get().get( this.id );

        if ( project == null )
        {
            return null;
        }

        final ProjectPermissions projectPermissions = this.projectService.get().getPermissions( project.getName() );

        final Boolean readAccess = GetProjectReadAccessCommand.create()
            .contentService( this.contentService.get() )
            .projectName( project.getName() )
            .build()
            .execute();

        final Locale language =
            GetProjectLanguageCommand.create().projectName( this.id ).contentService( this.contentService.get() ).build().execute();

        return ProjectMapper.create()
            .setProject( project )
            .setLanguage( language )
            .setProjectPermissions( projectPermissions )
            .setIsPublic( readAccess )
            .build();
    }

    public void setId( final String value )
    {
        this.id = ProjectName.from( Objects.requireNonNull( value, "Project name is required" ) );
    }
}
