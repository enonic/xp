package com.enonic.xp.lib.project;

import java.util.Locale;

import com.enonic.xp.lib.project.command.GetProjectLanguageCommand;
import com.enonic.xp.lib.project.command.GetProjectReadAccessCommand;
import com.enonic.xp.lib.project.mapper.ProjectMapper;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectPermissions;

public final class GetProjectHandler
    extends BaseProjectHandler
{
    private ProjectName id;

    @Override
    protected ProjectMapper doExecute()
    {
        final Project project = this.projectService.get( this.id );

        if ( project == null )
        {
            return null;
        }

        final ProjectPermissions projectPermissions = !ProjectConstants.DEFAULT_PROJECT_NAME.equals( project.getName() )
            ? this.projectService.getPermissions( project.getName() )
            : null;

        final Boolean readAccess =
            !ProjectConstants.DEFAULT_PROJECT_NAME.equals( project.getName() ) ? GetProjectReadAccessCommand.create().contentService(
                this.contentService ).projectName( project.getName() ).build().execute() : null;

        final Locale language = GetProjectLanguageCommand.create().
            projectName( this.id ).
            contentService( this.contentService ).
            build().
            execute();

        return ProjectMapper.create().
            setProject( project ).
            setLanguage( language ).
            setProjectPermissions( projectPermissions ).
            setIsPublic( readAccess ).
            build();
    }

    public void setId( final String value )
    {
        this.id = ProjectName.from( value );
    }
}
