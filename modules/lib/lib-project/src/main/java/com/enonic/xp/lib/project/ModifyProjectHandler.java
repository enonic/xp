package com.enonic.xp.lib.project;

import java.util.Locale;

import com.google.common.base.Preconditions;

import com.enonic.xp.lib.project.command.ApplyProjectLanguageCommand;
import com.enonic.xp.lib.project.command.GetProjectLanguageCommand;
import com.enonic.xp.lib.project.command.GetProjectReadAccessCommand;
import com.enonic.xp.lib.project.mapper.ProjectMapper;
import com.enonic.xp.project.ModifyProjectParams;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectPermissions;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.site.SiteConfigs;

public final class ModifyProjectHandler
    extends BaseProjectHandler
{
    private ProjectName id;

    private String displayName;

    private String description;

    private Locale language;

    private SiteConfigs siteConfigs;

    @Override
    protected ProjectMapper doExecute()
    {
        final Project projectBeforeUpdate = this.projectService.get().get( this.id );
        final ModifyProjectParams params = modifyProjectParams( projectBeforeUpdate );

        final Project project = this.projectService.get().modify( params );

        Locale projectLanguage;
        if ( this.language != null )
        {
            projectLanguage = ApplyProjectLanguageCommand.create()
                .projectName( this.id )
                .language( this.language )
                .contentService( this.contentService.get() )
                .build()
                .execute();
        }
        else
        {
            projectLanguage =
                GetProjectLanguageCommand.create().projectName( this.id ).contentService( this.contentService.get() ).build().execute();
        }

        final Boolean isPublic =
            GetProjectReadAccessCommand.create().projectName( this.id ).contentService( this.contentService.get() ).build().execute();

        final ProjectPermissions projectPermissions = this.projectService.get().getPermissions( this.id );

        return ProjectMapper.create()
            .setProject( project )
            .setLanguage( projectLanguage )
            .setProjectPermissions( projectPermissions )
            .setIsPublic( isPublic )
            .build();
    }

    private ModifyProjectParams modifyProjectParams( final Project projectBeforeUpdate )
    {
        final ModifyProjectParams.Builder params = ModifyProjectParams.create()
            .name( this.id )
            .displayName( this.displayName != null ? this.displayName : projectBeforeUpdate.getDisplayName() )
            .description( this.description != null ? this.description : projectBeforeUpdate.getDescription() );

        final SiteConfigs siteConfigs = this.siteConfigs != null ? this.siteConfigs : projectBeforeUpdate.getSiteConfigs();

        siteConfigs.forEach( params::addSiteConfig );

        return params.build();
    }

    @Override
    protected void validate()
    {
        Preconditions.checkArgument( this.displayName == null || !this.displayName.isBlank(), "Project display name cannot be empty" );
    }

    public void setId( final String value )
    {
        this.id = ProjectName.from( value );
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

    public void setSiteConfig( final ScriptValue value )
    {
        this.siteConfigs = value != null ? SiteConfigs.from( buildSiteConfigs( value ) ) : null;
    }
}
