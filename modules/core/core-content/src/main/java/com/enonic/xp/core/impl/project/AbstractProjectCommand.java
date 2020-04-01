package com.enonic.xp.core.impl.project;

import com.google.common.base.Preconditions;

import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectRole;
import com.enonic.xp.security.PrincipalKey;

abstract class AbstractProjectCommand
{
    final ProjectName projectName;

    AbstractProjectCommand( final Builder builder )
    {
        this.projectName = builder.projectName;
    }

    protected PrincipalKey createRoleKey( final ProjectRole projectRole )
    {
        final String roleName = ProjectConstants.PROJECT_NAME_PREFIX + projectName + "." + projectRole.name().toLowerCase();
        return PrincipalKey.ofRole( roleName );
    }

    public static class Builder<B extends Builder>
    {
        ProjectName projectName;

        public B projectName( final ProjectName projectName )
        {
            this.projectName = projectName;
            return (B) this;
        }

        void validate()
        {
            Preconditions.checkNotNull( projectName, "Project name cannot be null" );
        }

    }

}
