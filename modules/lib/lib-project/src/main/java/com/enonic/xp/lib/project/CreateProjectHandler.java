package com.enonic.xp.lib.project;

import java.util.Locale;

import com.enonic.xp.lib.project.command.ApplyProjectLanguageCommand;
import com.enonic.xp.lib.project.command.ApplyProjectReadAccessCommand;
import com.enonic.xp.lib.project.mapper.ProjectMapper;
import com.enonic.xp.project.CreateProjectParams;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectPermissions;
import com.enonic.xp.script.ScriptValue;

public final class CreateProjectHandler
    extends BaseProjectHandler
{
    private ProjectName name;

    private String displayName;

    private String description;

    private Locale language;

    private ProjectPermissions permissions;

    private boolean isPublic;

    @Override
    protected ProjectMapper doExecute()
    {
        final CreateProjectParams params = createProjectParams();
        final Project project = this.projectService.create( params );

        final Locale modifiedLanguage = this.language != null ? ApplyProjectLanguageCommand.create().
            projectName( this.name ).
            language( this.language ).
            contentService( this.contentService ).
            build().
            execute() : null;

        final ProjectPermissions modifiedPermissions = this.permissions != null
            ? this.projectService.modifyPermissions( this.name, this.permissions )
            : ProjectPermissions.create().build();

        final Boolean modifiedIsPublic = ApplyProjectReadAccessCommand.create().
            setPublic( this.isPublic ).
            contentService( this.contentService ).
            projectName( this.name ).
            build().
            execute();

        return ProjectMapper.create().
            setProject( project ).
            setLanguage( modifiedLanguage ).
            setProjectPermissions( modifiedPermissions ).
            setIsPublic( modifiedIsPublic ).
            build();
    }

    private CreateProjectParams createProjectParams()
    {
        return CreateProjectParams.create().
            name( this.name ).
            displayName( this.displayName ).
            description( this.description ).
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

    public void setPermissions( final ScriptValue value )
    {
        this.permissions = buildProjectPermissions( value );
    }

    public void setReadAccess( final ScriptValue value )
    {
        this.isPublic = buildReadAccess( value );
    }
}