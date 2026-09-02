package com.enonic.xp.app.system;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.index.ReindexParams;
import com.enonic.xp.index.ReindexResult;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.task.TaskProgressReporterContext;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.testing.ScriptTestSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReindexTaskHandlerTest
    extends ScriptTestSupport
{
    private static final RepositoryId REPO = RepositoryId.from( "my-repo" );

    @Captor
    private ArgumentCaptor<ReindexParams> paramsCaptor;

    @Mock
    private IndexService indexService;

    @Mock
    private RepositoryService repositoryService;

    @Mock
    private TaskService taskService;

    @Mock
    private ProgressReporter progressReporter;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();
        addService( IndexService.class, this.indexService );
        addService( RepositoryService.class, this.repositoryService );
        addService( TaskService.class, this.taskService );
    }

    @Test
    void reindex()
    {
        final TaskId taskId = task();
        when( indexService.reindex( any( ReindexParams.class ) ) ).thenReturn( result( Branch.from( "master" ) ) );

        TaskProgressReporterContext.withContext( ( id, reporter ) -> runFunction( "/test/ReindexTaskHandlerTest.js", "reindex" ) )
            .run( taskId, progressReporter );

        verify( indexService ).reindex( paramsCaptor.capture() );
        assertEquals( REPO, paramsCaptor.getValue().getRepositoryId() );
        assertEquals( 1, paramsCaptor.getValue().getBranches().getSize() );
        assertTrue( paramsCaptor.getValue().getBranches().contains( Branch.from( "master" ) ) );
        assertTrue( paramsCaptor.getValue().isInitialize() );
        assertNotNull( paramsCaptor.getValue().getListener() );
        verify( progressReporter ).progress( any() );
    }

    @Test
    void reindexDefaultsToRepositoryBranches()
    {
        final TaskId taskId = task();
        when( repositoryService.get( REPO ) ).thenReturn(
            Repository.create().id( REPO ).branches( Branch.from( "draft" ), Branch.from( "master" ) ).build() );
        when( indexService.reindex( any( ReindexParams.class ) ) ).thenReturn( result( Branch.from( "draft" ), Branch.from( "master" ) ) );

        TaskProgressReporterContext.withContext( ( id, reporter ) -> runFunction( "/test/ReindexTaskHandlerTest.js", "reindexDefault" ) )
            .run( taskId, progressReporter );

        verify( indexService ).reindex( paramsCaptor.capture() );
        assertEquals( 2, paramsCaptor.getValue().getBranches().getSize() );
        assertFalse( paramsCaptor.getValue().isInitialize() );
    }

    private TaskId task()
    {
        final TaskId taskId = TaskId.from( "task" );
        when( taskService.getTaskInfo( taskId ) ).thenReturn( TaskInfo.create()
                                                                  .id( taskId )
                                                                  .name( "com.enonic.xp.app.system:reindex" )
                                                                  .application( ApplicationKey.SYSTEM )
                                                                  .startTime( Instant.now() )
                                                                  .build() );
        return taskId;
    }

    private static ReindexResult result( final Branch... branches )
    {
        return ReindexResult.create()
            .repositoryId( REPO )
            .startTime( Instant.EPOCH )
            .endTime( Instant.EPOCH.plus( Duration.ofSeconds( 1 ) ) )
            .duration( Duration.ofSeconds( 1 ) )
            .branches( Branches.from( branches ) )
            .build();
    }
}
