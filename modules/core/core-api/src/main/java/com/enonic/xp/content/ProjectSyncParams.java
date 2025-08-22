package com.enonic.xp.content;

import java.util.Objects;

import com.enonic.xp.project.ProjectName;

public final class ProjectSyncParams
{
    private final ProjectName targetProject;

    private ProjectSyncParams( final Builder builder )
    {
        this.targetProject = builder.targetProject;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ProjectName getTargetProject()
    {
        return targetProject;
    }

    public static final class Builder
    {
        private ProjectName targetProject;

        private Builder()
        {
        }

        public Builder targetProject( final ProjectName targetProject )
        {
            this.targetProject = targetProject;
            return this;
        }

        private void validate()
        {
            Objects.requireNonNull( targetProject, "targetProject is required" );
        }

        public ProjectSyncParams build()
        {
            validate();
            return new ProjectSyncParams( this );
        }

    }
}
