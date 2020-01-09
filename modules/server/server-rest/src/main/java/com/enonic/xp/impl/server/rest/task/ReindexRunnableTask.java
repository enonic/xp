package com.enonic.xp.impl.server.rest.task;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.common.base.Splitter;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.impl.server.rest.model.ReindexRequestJson;
import com.enonic.xp.impl.server.rest.model.ReindexResultJson;
import com.enonic.xp.impl.server.rest.task.listener.ReindexListenerImpl;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.index.ReindexParams;
import com.enonic.xp.index.ReindexResult;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.task.AbstractRunnableTask;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskId;

public class ReindexRunnableTask
    extends AbstractRunnableTask
{
    private final ReindexRequestJson params;

    private IndexService indexService;

    private ReindexRunnableTask( Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.indexService = builder.indexService;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public void run( final TaskId id, final ProgressReporter progressReporter )
    {
        final ReindexResult result = this.indexService.reindex( ReindexParams.create().
            setBranches( parseBranches( params.branches ) ).
            listener( new ReindexListenerImpl( progressReporter ) ).
            initialize( params.initialize ).
            repositoryId( parseRepositoryId( params.repository ) ).
            build() );

        progressReporter.info( ReindexResultJson.create( result ).toString() );
    }

    public static Branches parseBranches( final String branches )
    {
        final List<Branch> parsed = StreamSupport.stream( Splitter.on( "," ).split( branches ).spliterator(), false ).
            map( Branch::from ).collect( Collectors.toList() );
        return Branches.from( parsed );
    }

    public static RepositoryId parseRepositoryId( final String repository )
    {
        return RepositoryId.from( repository );
    }


    public static class Builder
        extends AbstractRunnableTask.Builder<Builder>
    {
        private ReindexRequestJson params;

        private IndexService indexService;

        public Builder params( ReindexRequestJson params )
        {
            this.params = params;
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
