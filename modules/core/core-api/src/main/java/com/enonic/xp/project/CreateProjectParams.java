package com.enonic.xp.project;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.security.acl.AccessControlList;

@PublicApi
public final class CreateProjectParams
{
    private final ProjectName name;

    private final String displayName;

    private final String description;

    private final ProjectName parent;

    private final AccessControlList permissions;

    private final ApplicationKeys applications;

    private final boolean forceInitialization;

    private CreateProjectParams( final Builder builder )
    {
        this.name = builder.name;
        this.displayName = builder.displayName;
        this.description = builder.description;
        this.parent = builder.parent;
        this.applications = ApplicationKeys.from( builder.applications.build() );
        this.forceInitialization = builder.forceInitialization;
        this.permissions = builder.permissions;
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

    public ProjectName getParent()
    {
        return parent;
    }

    public ApplicationKeys getApplications()
    {
        return applications;
    }

    public AccessControlList getPermissions()
    {
        return permissions;
    }

    public boolean isForceInitialization()
    {
        return forceInitialization;
    }

    public static final class Builder
    {

        private ProjectName name;

        private String displayName;

        private String description;

        private ProjectName parent;

        private AccessControlList permissions;

        private boolean forceInitialization = false;

        private final ImmutableList.Builder<ApplicationKey> applications = ImmutableList.builder();

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

        public Builder parent( final ProjectName parent )
        {
            this.parent = parent;
            return this;
        }

        public Builder permissions( final AccessControlList permissions )
        {
            this.permissions = permissions;
            return this;
        }

        public Builder forceInitialization( final boolean forceInitialization )
        {
            this.forceInitialization = forceInitialization;
            return this;
        }

        public Builder addApplication( final ApplicationKey application )
        {
            this.applications.add( application );
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
