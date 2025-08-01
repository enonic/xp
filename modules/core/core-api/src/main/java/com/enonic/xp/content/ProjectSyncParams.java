package com.enonic.xp.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.project.ProjectName;

public final class ProjectSyncParams
{
    private final ProjectName targetProject;

    public ProjectSyncParams( Builder builder )
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
            Preconditions.checkNotNull( targetProject, "targetProject must be set." );
        }

        public ProjectSyncParams build()
        {
            validate();
            return new ProjectSyncParams( this );
        }

    }
}
