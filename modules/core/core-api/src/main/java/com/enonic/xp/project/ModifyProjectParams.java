package com.enonic.xp.project;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class ModifyProjectParams
{
    private final ProjectName name;

    private final String displayName;

    private final String description;

    private ModifyProjectParams( final Builder builder )
    {
        this.name = builder.name;
        this.displayName = builder.displayName;
        this.description = builder.description;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Deprecated
    public static Builder create( final CreateProjectParams params )
    {
        return create().
            name( params.getName() ).
            description( params.getDescription() ).
            displayName( params.getDisplayName() );
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

    public static final class Builder
    {
        private ProjectName name;

        private String displayName;

        private String description;

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
