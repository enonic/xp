package com.enonic.xp.impl.server.rest.task;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.impl.server.rest.model.ReindexRequestJson;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.index.ReindexParams;
import com.enonic.xp.index.ReindexResult;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.support.JsonTestHelper;
import com.enonic.xp.task.ProgressReportParams;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.task.TaskService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReindexRunnableTaskTest
{
    JsonTestHelper jsonTestHelper = new JsonTestHelper( this );

    IndexService indexService;

    TaskService taskService;

    @BeforeEach
    void setUp()
    {
        this.indexService = mock( IndexService.class );
        this.taskService = mock( TaskService.class );
    }

    private ReindexRunnableTask createTask( final ReindexRequestJson params )
    {
        return ReindexRunnableTask.create()
            .taskService( taskService )
            .indexService( indexService )
            .repository( params.getRepository() )
            .branches( params.getBranches() )
            .initialize( params.isInitialize() )
            .build();
    }

    @Test
    void reindex()
    {
        ReindexResult reindexResult = ReindexResult.create().
        repositoryId( RepositoryId.from( "repo" ) ).
        branches( Branches.from( Branch.from( "draft" ), Branch.from( "master" )  ) ).
        duration( Duration.ofMillis( 41416 ) ).
        startTime( Instant.ofEpochMilli( 1438866915875L ) ).
        endTime( Instant.ofEpochMilli( 1438866957291L ) ).
        build();

        when( this.indexService.reindex( any( ReindexParams.class ) ) ).thenReturn( reindexResult );

        final ReindexRunnableTask task = createTask( new ReindexRequestJson( "repo", "draft,master", true ) );

        ProgressReporter progressReporter = mock( ProgressReporter.class );

        final TaskId taskId = TaskId.from( "taskId" );

        when( taskService.getTaskInfo( taskId ) ).thenReturn(
            TaskInfo.create().id( taskId ).name( "reindex" ).application( ApplicationKey.SYSTEM ).startTime( Instant.now() ).build() );

        task.run( taskId, progressReporter );

        final ArgumentCaptor<String> progressReporterCaptor = ArgumentCaptor.forClass( String.class );
        verify( progressReporter, times( 1 ) ).progress( ProgressReportParams.create( progressReporterCaptor.capture() ).build() );

        final String result = progressReporterCaptor.getValue();
        jsonTestHelper.assertJsonEquals( jsonTestHelper.loadTestJson( "reindex_result.json" ), jsonTestHelper.stringToJson( result ) );
    }

}
