package com.enonic.xp.project;

import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.project.layer.ContentLayer;
import com.enonic.xp.project.layer.ContentLayerKey;
import com.enonic.xp.project.layer.ContentLayerKeys;
import com.enonic.xp.project.layer.ContentLayers;
import com.enonic.xp.repository.Repository;

@PublicApi
public final class Project
{
    private final ProjectName name;

    private final String displayName;

    private final String description;

    private final Attachment icon;

    private final ContentLayers layers;

    private final ProjectPermissions permissions;

    private Project( Builder builder )
    {
        this.name = builder.name;
        this.displayName = builder.displayName;
        this.description = builder.description;
        this.icon = builder.icon;
        this.layers = builder.layers.build();
        this.permissions = builder.permissions.build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final Project source )
    {
        return new Builder( source );
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
            displayName( projectData.getString( ProjectConstants.PROJECT_DISPLAY_NAME_PROPERTY ) );

        buildIcon( project, projectData );
        buildLayers( project, projectData );
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

    private static void buildLayers( final Project.Builder project, final PropertySet projectData )
    {
        final Iterable<PropertySet> layerSets = projectData.getSets( ProjectConstants.PROJECT_LAYERS_PROPERTY );

        if ( layerSets != null )
        {
            layerSets.forEach( layerSet -> project.addLayer( ContentLayer.from( layerSet ) ) );
        }
    }

    private static void buildPermissions( final Project.Builder project, final PropertySet projectData )
    {
        final PropertySet permissionsSet = projectData.getPropertySet( ProjectConstants.PROJECT_PERMISSIONS_PROPERTY );

        if ( permissionsSet != null )
        {
            final Iterable<String> ownerKeys = permissionsSet.getStrings( ProjectConstants.PROJECT_ACCESS_LEVEL_OWNER_PROPERTY );
            final Iterable<String> expertKeys = permissionsSet.getStrings( ProjectConstants.PROJECT_ACCESS_LEVEL_EXPERT_PROPERTY );
            final Iterable<String> contributorKeys =
                permissionsSet.getStrings( ProjectConstants.PROJECT_ACCESS_LEVEL_CONTRIBUTOR_PROPERTY );

            final ProjectPermissions.Builder projectPermissions = ProjectPermissions.create();
            if ( ownerKeys != null )
            {
                ownerKeys.forEach( projectPermissions::addOwner );
            }

            if ( expertKeys != null )
            {
                expertKeys.forEach( projectPermissions::addExpert );
            }

            if ( contributorKeys != null )
            {
                contributorKeys.forEach( projectPermissions::addContributor );
            }

            project.addPermissions( projectPermissions.build() );
        }
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

    public ContentLayers getLayers()
    {
        return layers;
    }

    public ContentLayer getLayer( final ContentLayerKey contentLayerKey )
    {
        return layers.getLayer( contentLayerKey );
    }

    public ContentLayerKeys getKeys()
    {
        return layers.getKeys();
    }

    public ProjectPermissions getPermissions()
    {
        return permissions;
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
            Objects.equals( description, project.description ) && Objects.equals( icon, project.icon ) &&
            Objects.equals( layers, project.layers ) && Objects.equals( permissions, project.permissions );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( name, displayName, description, icon, layers, permissions );
    }

    public static final class Builder
    {
        private ProjectName name;

        private String displayName;

        private String description;

        private Attachment icon;

        private ContentLayers.Builder layers = ContentLayers.create();

        private ProjectPermissions.Builder permissions;

        private Builder()
        {
            permissions = ProjectPermissions.create();
        }

        private Builder( final Project source )
        {
            this.name = source.name;
            this.displayName = source.displayName;
            this.description = source.description;
            this.icon = source.icon;
            this.layers.addAll( source.layers.getSet() );
            this.permissions = ProjectPermissions.create( source.permissions );
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

        public Builder addLayer( final ContentLayer contentLayer )
        {
            if ( contentLayer != null )
            {
                this.layers.add( contentLayer );
            }
            return this;
        }

        public Builder addPermissions( final ProjectPermissions projectPermissions )
        {
            if ( projectPermissions != null )
            {
                projectPermissions.getOwner().forEach( this.permissions::addOwner );
                projectPermissions.getExpert().forEach( this.permissions::addExpert );
                projectPermissions.getContributor().forEach( this.permissions::addContributor );
            }
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
