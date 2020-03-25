package com.enonic.xp.project;

import java.util.Objects;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

import com.enonic.xp.attachment.CreateAttachment;

@Beta
public final class ModifyProjectParams
{
    private final ProjectName name;

    private final String displayName;

    private final String description;

    private final CreateAttachment icon;

    private ModifyProjectParams( final Builder builder )
    {
        this.name = builder.name;
        this.displayName = builder.displayName;
        this.description = builder.description;
        this.icon = builder.icon;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final CreateProjectParams params )
    {
        return create().
            name( params.getName() ).
            description( params.getDescription() ).
            displayName( params.getDisplayName() ).
            icon( params.getIcon() );
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
        final ModifyProjectParams that = (ModifyProjectParams) o;
        return Objects.equals( name, that.name ) && Objects.equals( displayName, that.displayName ) &&
            Objects.equals( description, that.description ) && Objects.equals( icon, that.icon );
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

        private CreateAttachment icon;

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
