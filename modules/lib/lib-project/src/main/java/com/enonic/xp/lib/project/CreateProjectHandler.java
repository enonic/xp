package com.enonic.xp.lib.project;

import java.util.Locale;

import com.google.common.base.Preconditions;

import com.enonic.xp.app.ApplicationKeys;
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

import static com.google.common.base.Strings.nullToEmpty;

public final class CreateProjectHandler
    extends BaseProjectHandler
{
    private ProjectName id;

    private String displayName;

    private String description;

    private ProjectName parent;

    private Locale language;

    private ProjectPermissions permissions;

    private ApplicationKeys applications;

    private boolean isPublic;

    @Override
    protected ProjectMapper doExecute()
    {
        final CreateProjectParams params = createProjectParams();
        final Project project = this.projectService.create( params );

        final Locale modifiedLanguage = this.language != null ? ApplyProjectLanguageCommand.create()
            .projectName( this.id )
            .language( this.language )
            .contentService( this.contentService )
            .build()
            .execute() : null;

        final ProjectPermissions modifiedPermissions = this.permissions != null
            ? this.projectService.modifyPermissions( this.id, this.permissions )
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
            .parent( this.parent )
            .forceInitialization( true );

        if ( isPublic )
        {
            builder.permissions( AccessControlList.create()
                                     .add( AccessControlEntry.create().principal( RoleKeys.EVERYONE ).allow( Permission.READ ).build() )
                                     .build() );
        }

        if ( applications != null )
        {
            applications.stream().forEach( builder::addApplication );
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

    public void setParent( final String value )
    {
        this.parent = value != null ? ProjectName.from( value ) : null;
    }

    public void setApplications( final String[] value )
    {
        this.applications = value != null ? ApplicationKeys.from( value ) : null;
    }
}
