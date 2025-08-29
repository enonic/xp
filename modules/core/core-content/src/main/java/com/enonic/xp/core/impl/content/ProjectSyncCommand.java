package com.enonic.xp.core.impl.content;

import java.util.Objects;

import com.enonic.xp.content.ProjectSyncParams;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectService;


final class ProjectSyncCommand
{
    private final ProjectSyncParams params;

    private final ProjectService projectService;

    private final ContentSynchronizer contentSynchronizer;

    private ProjectSyncCommand( final Builder builder )
    {
        this.params = builder.params;
        this.projectService = builder.projectService;
        this.contentSynchronizer = builder.contentSynchronizer;
    }

    public static Builder create( final ProjectSyncParams params )
    {
        return new Builder( params );
    }

    void execute()
    {
        final Project targetProject = projectService.get( params.getTargetProject() );

        if ( !targetProject.getParents().isEmpty() )
        {
            targetProject.getParents()
                .forEach( parent -> contentSynchronizer.sync(
                    ContentSyncParams.create().sourceProject( parent ).targetProject( params.getTargetProject() ).build() ) );
        }
        else
        {
            throw new IllegalArgumentException( String.format( "[%s] project has no parent.", params.getTargetProject() ) );
        }
    }

    public static class Builder
    {
        private final ProjectSyncParams params;

        private ProjectService projectService;

        private ContentSynchronizer contentSynchronizer;

        private Builder( final ProjectSyncParams params )
        {
            this.params = params;
        }

        public Builder contentSynchronizer( final ContentSynchronizer contentSynchronizer )
        {
            this.contentSynchronizer = contentSynchronizer;
            return this;
        }

        public Builder projectService( final ProjectService projectService )
        {
            this.projectService = projectService;
            return this;
        }

        void validate()
        {
            Objects.requireNonNull( this.projectService );
            Objects.requireNonNull( this.contentSynchronizer );
            Objects.requireNonNull( params, "params cannot be null" );
        }

        public ProjectSyncCommand build()
        {
            validate();
            return new ProjectSyncCommand( this );
        }
    }

}
