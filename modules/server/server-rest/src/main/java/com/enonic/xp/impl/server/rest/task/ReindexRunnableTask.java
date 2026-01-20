package com.enonic.xp.impl.server.rest.task;

import com.enonic.xp.branch.Branches;
import com.enonic.xp.impl.server.rest.model.ReindexResultJson;
import com.enonic.xp.impl.server.rest.task.listener.ReindexListenerImpl;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.index.ReindexParams;
import com.enonic.xp.index.ReindexResult;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.task.ProgressReportParams;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;

public class ReindexRunnableTask
    implements RunnableTask
{
    private final RepositoryId repository;

    private final Branches branches;

    private final boolean initialize;

    private final IndexService indexService;

    private final TaskService taskService;

    private ReindexRunnableTask( Builder builder )
    {
        this.repository = builder.repository;
        this.branches = builder.branches;
        this.initialize = builder.initialize;

        this.taskService = builder.taskService;
        this.indexService = builder.indexService;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public void run( final TaskId id, final ProgressReporter progressReporter )
    {
        TaskUtils.checkAlreadySubmitted( taskService.getTaskInfo( id ), taskService.getAllTasks() );
        final ReindexResult result = this.indexService.reindex( ReindexParams.create()
                                                                    .setBranches( branches )
                                                                    .listener( new ReindexListenerImpl( progressReporter ) )
                                                                    .initialize( initialize )
                                                                    .repositoryId( repository )
                                                                    .build() );

        progressReporter.progress( ProgressReportParams.create( ReindexResultJson.create( result ).toString() ).build() );
    }

    public static class Builder
    {
        private RepositoryId repository;

        private boolean initialize;

        private Branches branches;

        private IndexService indexService;

        private TaskService taskService;

        public Builder repository( final RepositoryId repository )
        {
            this.repository = repository;
            return this;
        }

        public Builder initialize( final boolean initialize )
        {
            this.initialize = initialize;
            return this;
        }

        public Builder branches( final Branches branches )
        {
            this.branches = branches;
            return this;
        }

        public Builder taskService( final TaskService taskService )
        {
            this.taskService = taskService;
            return this;
        }

        public Builder indexService( final IndexService indexService )
        {
            this.indexService = indexService;
            return this;
        }

        public ReindexRunnableTask build()
        {
            return new ReindexRunnableTask( this );
        }
    }
}
