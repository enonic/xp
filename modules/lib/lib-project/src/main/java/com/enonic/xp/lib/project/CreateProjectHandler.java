package com.enonic.xp.lib.project;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import com.enonic.xp.lib.project.command.ApplyProjectLanguageCommand;
import com.enonic.xp.lib.project.mapper.ProjectMapper;
import com.enonic.xp.project.CreateProjectParams;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectPermissions;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;

import static com.google.common.base.Strings.nullToEmpty;

public final class CreateProjectHandler
    extends BaseProjectHandler
{
    private ProjectName id;

    private String displayName;

    private String description;

    private List<ProjectName> parents;

    private Locale language;

    private ProjectPermissions permissions;

    private SiteConfigs siteConfigs;

    private boolean isPublic;

    @Override
    protected ProjectMapper doExecute()
    {
        final CreateProjectParams params = createProjectParams();
        final Project project = this.projectService.get().create( params );

        final Locale modifiedLanguage = this.language != null ? ApplyProjectLanguageCommand.create()
            .projectName( this.id )
            .language( this.language )
            .contentService( this.contentService.get() )
            .build()
            .execute() : null;

        final ProjectPermissions modifiedPermissions = this.permissions != null
            ? this.projectService.get().modifyPermissions( this.id, this.permissions )
            : ProjectPermissions.create().build();

        return ProjectMapper.create()
            .setProject( project )
            .setLanguage( modifiedLanguage )
            .setProjectPermissions( modifiedPermissions )
            .setIsPublic( isPublic )
            .build();
    }

    private CreateProjectParams createProjectParams()
    {
        final CreateProjectParams.Builder builder = CreateProjectParams.create()
            .name( this.id )
            .displayName( this.displayName )
            .description( this.description )
            .forceInitialization( true );

        if ( parents != null )
        {
            builder.addParents( parents );
        }

        if ( isPublic )
        {
            builder.permissions( AccessControlList.create()
                                     .add( AccessControlEntry.create().principal( RoleKeys.EVERYONE ).allow( Permission.READ ).build() )
                                     .build() );
        }

        if ( siteConfigs != null )
        {
            siteConfigs.forEach( builder::addSiteConfig );
        }

        return builder.build();
    }

    @Override
    protected void validate()
    {
        Preconditions.checkArgument( !nullToEmpty( this.displayName ).isBlank(), "Project display name is required" );
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

    public void setPermissions( final ScriptValue value )
    {
        this.permissions = buildProjectPermissions( value );
    }

    public void setReadAccess( final ScriptValue value )
    {
        this.isPublic = buildReadAccess( value );
    }

    public void setParents( final String[] values )
    {
        this.parents = values != null ? Arrays.stream( values ).map( ProjectName::from ).collect( Collectors.toList() ) : List.of();
    }

    public void setSiteConfig( final ScriptValue value )
    {
        final List<SiteConfig> configs = buildSiteConfigs( value );
        this.siteConfigs = configs != null ? SiteConfigs.from( configs ) : null;
    }
}
