package com.enonic.xp.lib.project;

import java.util.Locale;

import com.enonic.xp.lib.project.command.ApplyProjectLanguageCommand;
import com.enonic.xp.lib.project.command.GetProjectLanguageCommand;
import com.enonic.xp.lib.project.command.GetProjectReadAccessCommand;
import com.enonic.xp.lib.project.mapper.ProjectMapper;
import com.enonic.xp.project.ModifyProjectParams;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectPermissions;

public final class ModifyProjectHandler
    extends BaseProjectHandler
{
    private ProjectName name;

    private String displayName;

    private String description;

    private Locale language;

    @Override
    protected ProjectMapper doExecute()
    {
        final Project projectBeforeUpdate = this.projectService.get( this.name );
        final ModifyProjectParams params = modifyProjectParams( projectBeforeUpdate );

        final Project project = this.projectService.modify( params );

        Locale projectLanguage;
        if ( this.language != null )
        {
            projectLanguage = ApplyProjectLanguageCommand.create().
                projectName( this.name ).
                language( this.language ).
                contentService( this.contentService ).
                build().
                execute();
        }
        else
        {
            projectLanguage = GetProjectLanguageCommand.create().
                projectName( this.name ).
                contentService( this.contentService ).
                build().
                execute();
        }

        final Boolean isPublic = GetProjectReadAccessCommand.create().
            projectName( this.name ).
            contentService( this.contentService ).
            build().
            execute();

        final ProjectPermissions projectPermissions = this.projectService.getPermissions( this.name );

        return ProjectMapper.create().
            setProject( project ).
            setLanguage( projectLanguage ).
            setProjectPermissions( projectPermissions ).
            setIsPublic( isPublic ).
            build();
    }

    private ModifyProjectParams modifyProjectParams( final Project projectBeforeUpdate )
    {
        return ModifyProjectParams.create().
            name( this.name ).
            displayName( this.displayName != null ? this.displayName : projectBeforeUpdate.getDisplayName() ).
            description( this.description != null ? this.description : projectBeforeUpdate.getDescription() ).
            build();
    }

    public void setName( final String value )
    {
        this.name = ProjectName.from( value );
    }

    public void setDisplayName( final String value )
    {
        this.displayName = value;
    }

    public void setDescription( final String value )
    {
        this.description = value;
    }

    public void setLanguage( final String value )
    {
        this.language = buildLanguage( value );
    }
}