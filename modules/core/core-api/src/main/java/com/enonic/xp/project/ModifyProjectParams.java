package com.enonic.xp.project;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.attachment.AttachmentSerializer;
import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;

@PublicApi
public final class ModifyProjectParams
{
    private final ProjectName name;

    private final String displayName;

    private final String description;

    private final CreateAttachment icon;

    private final ProjectPermissions permissions;

    private ModifyProjectParams( final Builder builder )
    {
        this.name = builder.name;
        this.displayName = builder.displayName;
        this.description = builder.description;
        this.icon = builder.icon;
        this.permissions = builder.permissions;
    }

    public static Builder create( final CreateProjectParams params )
    {
        final Builder builder = create().
            name( params.getName() ).
            description( params.getDescription() ).
            displayName( params.getDisplayName() ).
            permissions( params.getPermissions() ).
            icon( params.getIcon() );

        return builder;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public PropertyTree toData()
    {
        final PropertyTree data = new PropertyTree();

        final PropertySet set = data.addSet( ProjectConstants.PROJECT_DATA_SET_NAME );
        set.addString( ProjectConstants.PROJECT_DESCRIPTION_PROPERTY, description );
        set.addString( ProjectConstants.PROJECT_DISPLAY_NAME_PROPERTY, displayName );
        if ( icon != null )
        {
            AttachmentSerializer.create( set, CreateAttachments.from( icon ), ProjectConstants.PROJECT_ICON_PROPERTY );
        }
        else
        {
            set.addSet( ProjectConstants.PROJECT_ICON_PROPERTY, null );
        }

        if ( permissions != null )
        {
            final PropertySet permissionsSet = set.addSet( ProjectConstants.PROJECT_PERMISSIONS_PROPERTY );
            permissionsSet.addStrings( ProjectConstants.PROJECT_ACCESS_LEVEL_OWNER_PROPERTY, permissions.getOwner().asStrings() );
            permissionsSet.addStrings( ProjectConstants.PROJECT_ACCESS_LEVEL_EXPERT_PROPERTY, permissions.getExpert().asStrings() );
            permissionsSet.addStrings( ProjectConstants.PROJECT_ACCESS_LEVEL_CONTRIBUTOR_PROPERTY,
                                       permissions.getContributor().asStrings() );
        }
        else
        {
            set.addSet( ProjectConstants.PROJECT_PERMISSIONS_PROPERTY, null );
        }

        return data;
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

    public CreateAttachment getIcon()
    {
        return icon;
    }

    public ProjectPermissions getPermissions()
    {
        return permissions;
    }

    public static final class Builder
    {
        private ProjectName name;

        private String displayName;

        private String description;

        private CreateAttachment icon;

        private ProjectPermissions permissions;

        private Builder()
        {
        }

        public Builder name( final ProjectName name )
        {
            this.name = name;
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

        public Builder icon( final CreateAttachment icon )
        {
            this.icon = icon;
            return this;
        }

        public Builder permissions( final ProjectPermissions permissions )
        {
            this.permissions = permissions;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( name, "projectName cannot be null" );
        }

        public ModifyProjectParams build()
        {
            validate();
            return new ModifyProjectParams( this );
        }
    }
}
