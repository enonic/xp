package com.enonic.xp.core.impl.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.project.ProjectName;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.TaskId;

final class ContentSyncTask
    implements RunnableTask
{
    private final ProjectName sourceProject;

    private final ProjectName targetProject;

    private final ContentSynchronizer contentSynchronizer;

    public ContentSyncTask( final Builder builder )
    {
        this.contentSynchronizer = builder.contentSynchronizer;
        this.sourceProject = builder.sourceProject;
        this.targetProject = builder.targetProject;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public void run( final TaskId id, final ProgressReporter progressReporter )
    {
        contentSynchronizer.sync( ContentSyncParams.create().
            sourceProject( sourceProject ).
            targetProject( targetProject ).
            build() );
    }

    public static class Builder
    {
        private ContentSynchronizer contentSynchronizer;

        private ProjectName sourceProject;

        private ProjectName targetProject;

        private Builder()
        {
        }

        public Builder contentSynchronizer( final ContentSynchronizer contentSynchronizer )
        {
            this.contentSynchronizer = contentSynchronizer;
            return this;
        }

        public Builder sourceProject( final ProjectName sourceProject )
        {
            this.sourceProject = sourceProject;
            return this;
        }

        public Builder targetProject( final ProjectName targetProject )
        {
            this.targetProject = targetProject;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( this.contentSynchronizer, "contentSynchronizer must be set." );
            Preconditions.checkNotNull( this.sourceProject, "sourceProject must be set." );
            Preconditions.checkNotNull( this.targetProject, "targetProject must be set." );
        }

        public ContentSyncTask build()
        {
            validate();
            return new ContentSyncTask( this );
        }
    }
}
