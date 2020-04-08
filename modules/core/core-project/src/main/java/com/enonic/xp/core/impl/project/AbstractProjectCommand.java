package com.enonic.xp.core.impl.project;

import com.google.common.base.Preconditions;

import com.enonic.xp.project.ProjectName;

abstract class AbstractProjectCommand
{
    final ProjectName projectName;

    AbstractProjectCommand( final Builder builder )
    {
        this.projectName = builder.projectName;
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
