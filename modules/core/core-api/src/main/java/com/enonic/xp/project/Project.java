package com.enonic.xp.project;

import java.util.Objects;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;

@Beta
public final class Project
{
    private final ProjectName name;

    private final String displayName;

    private final String description;

    private final Attachment icon;

    private final ProjectPermissions permissions;

    private final ProjectReadAccess readAccess;

    private Project( Builder builder )
    {
        this.name = builder.name;
        this.displayName = builder.displayName;
        this.description = builder.description;
        this.icon = builder.icon;
        this.permissions = builder.permissions.build();
        this.readAccess = builder.readAccess;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Project from( final Repository repository )
    {
        if ( repository == null )
        {
            return null;
        }

        final PropertyTree repositoryData = repository.getData();

        //TODO: remove default project data for XP8
        if ( repositoryData == null )
        {
            return ContentConstants.CONTENT_REPO_ID.equals( repository.getId() ) ? ProjectConstants.DEFAULT_PROJECT : null;
        }

        final PropertySet projectData = repositoryData.getSet( ProjectConstants.PROJECT_DATA_SET_NAME );

        if ( projectData == null )
        {
            return ContentConstants.CONTENT_REPO_ID.equals( repository.getId() ) ? ProjectConstants.DEFAULT_PROJECT : null;
        }

        final Project.Builder project = Project.create().
            name( ProjectName.from( repository.getId() ) ).
            description( projectData.getString( ProjectConstants.PROJECT_DESCRIPTION_PROPERTY ) ).
            displayName( projectData.getString( ProjectConstants.PROJECT_DISPLAY_NAME_PROPERTY ) ).
            readAccess( getReadAccessFromData( projectData ) );

        buildIcon( project, projectData );
        buildPermissions( project, projectData );

        return project.build();
    }

    private static void buildIcon( final Project.Builder project, final PropertySet projectData )
    {
        final PropertySet iconData = projectData.getPropertySet( ProjectConstants.PROJECT_ICON_PROPERTY );

        if ( iconData != null )
        {
            project.icon( Attachment.create().
                name( iconData.getString( ContentPropertyNames.ATTACHMENT_NAME ) ).
                label( iconData.getString( ContentPropertyNames.ATTACHMENT_LABEL ) ).
                mimeType( iconData.getString( ContentPropertyNames.ATTACHMENT_MIMETYPE ) ).
                size( iconData.getLong( ContentPropertyNames.ATTACHMENT_SIZE ) ).
                textContent( iconData.getString( ContentPropertyNames.ATTACHMENT_TEXT ) ).
                build() );
        }
    }

    private static void buildPermissions( final Project.Builder project, final PropertySet projectData )
    {
        final PropertySet permissionsSet = projectData.getPropertySet( ProjectConstants.PROJECT_PERMISSIONS_PROPERTY );

        if ( permissionsSet != null )
        {
            final Iterable<String> ownerKeys = permissionsSet.getStrings( ProjectConstants.PROJECT_ACCESS_LEVEL_OWNER_PROPERTY );
            final Iterable<String> editorKeys = permissionsSet.getStrings( ProjectConstants.PROJECT_ACCESS_LEVEL_EDITOR_PROPERTY );
            final Iterable<String> authorKeys = permissionsSet.getStrings( ProjectConstants.PROJECT_ACCESS_LEVEL_AUTHOR_PROPERTY );
            final Iterable<String> contributorKeys =
                permissionsSet.getStrings( ProjectConstants.PROJECT_ACCESS_LEVEL_CONTRIBUTOR_PROPERTY );

            final ProjectPermissions.Builder projectPermissions = ProjectPermissions.create();
            if ( ownerKeys != null )
            {
                ownerKeys.forEach( projectPermissions::addOwner );
            }

            if ( editorKeys != null )
            {
                editorKeys.forEach( projectPermissions::addEditor );
            }

            if ( authorKeys != null )
            {
                authorKeys.forEach( projectPermissions::addAuthor );
            }

            if ( contributorKeys != null )
            {
                contributorKeys.forEach( projectPermissions::addContributor );
            }

            project.addPermissions( projectPermissions.build() );
        }
    }

    private static ProjectReadAccess getReadAccessFromData( final PropertySet projectData )
    {
        final PropertySet readAccessSet = projectData.getPropertySet( ProjectConstants.PROJECT_READ_ACCESS_PROPERTY );

        if ( readAccessSet == null )
        {
            return new ProjectReadAccess( ProjectReadAccessType.PUBLIC );
        }

        final ProjectReadAccessType type =
            ProjectReadAccessType.valueOf( readAccessSet.getString( ProjectConstants.PROJECT_READ_ACCESS_TYPE_PROPERTY ) );

        if ( type == ProjectReadAccessType.CUSTOM )
        {
            final PrincipalKeys.Builder builder = PrincipalKeys.create();
            readAccessSet.getStrings( ProjectConstants.PROJECT_READ_ACCESS_PRINCIPALS_PROPERTY ).forEach(
                principal -> builder.add( PrincipalKey.from( principal ) ) );

            return new ProjectReadAccess( ProjectReadAccessType.CUSTOM, builder.build() );
        }

        return new ProjectReadAccess( type );
    }

    public ProjectName getName()
    {
        return name;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getDescription()
    {
        return description;
    }

    public Attachment getIcon()
    {
        return icon;
    }

    public ProjectPermissions getPermissions()
    {
        return permissions;
    }

    public ProjectReadAccess getReadAccess()
    {
        return this.readAccess;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final Project project = (Project) o;
        return Objects.equals( name, project.name ) && Objects.equals( displayName, project.displayName ) &&
            Objects.equals( description, project.description ) && Objects.equals( icon, project.icon );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( name, displayName, description, icon );
    }

    public static final class Builder
    {

        private ProjectName name;

        private String displayName;

        private String description;

        private Attachment icon;

        private ProjectPermissions.Builder permissions = ProjectPermissions.create();

        private ProjectReadAccess readAccess = new ProjectReadAccess( ProjectReadAccessType.PRIVATE );

        private Builder()
        {
        }

        public Builder name( final ProjectName value )
        {
            this.name = value;
            return this;
        }

        public Builder displayName( final String displayName )
        {
            this.displayName = displayName;
            return this;
        }

        public Builder description( final String description )
        {
            this.description = description;
            return this;
        }

        public Builder icon( final Attachment icon )
        {
            this.icon = icon;
            return this;
        }

        public Builder addPermissions( final ProjectPermissions projectPermissions )
        {
            if ( projectPermissions != null )
            {
                projectPermissions.getOwner().forEach( this.permissions::addOwner );
                projectPermissions.getEditor().forEach( this.permissions::addEditor );
                projectPermissions.getAuthor().forEach( this.permissions::addAuthor );
                projectPermissions.getContributor().forEach( this.permissions::addContributor );
            }
            return this;
        }

        public Builder readAccess( final ProjectReadAccess readAccess )
        {
            this.readAccess = readAccess;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( name, "name cannot be null" );
        }

        public Project build()
        {
            validate();
            return new Project( this );
        }
    }
}
