package com.enonic.xp.core.impl.project;

import java.util.Objects;

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
            Objects.requireNonNull( projectName, "projectName is required" );
        }

    }

}
