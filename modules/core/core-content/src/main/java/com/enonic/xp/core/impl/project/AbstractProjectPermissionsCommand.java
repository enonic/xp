package com.enonic.xp.core.impl.project;

import com.google.common.base.Preconditions;

import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.security.SecurityService;

abstract class AbstractProjectPermissionsCommand
{
    final ProjectName projectName;

    final SecurityService securityService;

    AbstractProjectPermissionsCommand( final Builder builder )
    {
        this.securityService = builder.securityService;
        this.projectName = builder.projectName;
    }

    public static class Builder<B extends Builder>
    {
        SecurityService securityService;

        ProjectName projectName;

        @SuppressWarnings("unchecked")
        public B securityService( final SecurityService securityService )
        {
            this.securityService = securityService;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B projectName( final ProjectName projectName )
        {
            this.projectName = projectName;
            return (B) this;
        }

        void validate()
        {
            Preconditions.checkNotNull( securityService, "securityService cannot be null" );
            Preconditions.checkNotNull( projectName, "Project name cannot be null" );
            Preconditions.checkArgument( !ProjectConstants.DEFAULT_PROJECT_NAME.equals( projectName ), "Default project has no roles" );
        }

    }

}
