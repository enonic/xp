package com.enonic.xp.project;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.attachment.CreateAttachment;

@PublicApi
public final class CreateProjectParams
{
    private final ProjectName name;

    private final String displayName;

    private final String description;

    private final CreateAttachment icon;

    private CreateProjectParams( final Builder builder )
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

        public CreateProjectParams build()
        {
            validate();
            return new CreateProjectParams( this );
        }
    }
}
