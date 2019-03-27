package com.enonic.xp.impl.server.rest.task;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.dump.BranchDumpResult;
import com.enonic.xp.dump.DumpError;
import com.enonic.xp.dump.DumpService;
import com.enonic.xp.dump.RepoDumpResult;
import com.enonic.xp.dump.SystemDumpParams;
import com.enonic.xp.dump.SystemDumpResult;
import com.enonic.xp.impl.server.rest.model.SystemDumpRequestJson;
import com.enonic.xp.impl.server.rest.task.listener.SystemDumpListenerImpl;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.task.AbstractRunnableTaskTest;
import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.TaskId;

public class DumpRunnableTaskTest
    extends AbstractRunnableTaskTest
{
    private DumpService dumpService;

    @Before
    @Override
    public void setUp()
        throws Exception
    {
        super.setUp();
        this.dumpService = Mockito.mock( DumpService.class );
    }

    protected DumpRunnableTask createAndRunTask()
    {
        return null;
    }

    protected DumpRunnableTask createAndRunTask( final SystemDumpRequestJson params )
    {
        final DumpRunnableTask task = DumpRunnableTask.create().
            description( "dump" ).
            taskService( taskService ).
            dumpService( dumpService ).
            params( params ).
            build();

        task.run( TaskId.from( "taskId" ), progressReporter );

        return task;
    }

    @Test
    public void dump()
    {
        final SystemDumpResult systemDumpResult = SystemDumpResult.create().
            add( RepoDumpResult.create( RepositoryId.from( "my-repo" ) ).versions( 3L ).
                add( BranchDumpResult.create( Branch.create().value( "branch-value" ).build() ).addedNodes( 3 ).error(
                    DumpError.error( "error-message" ) ).build() ).build() ).
            build();

        final SystemDumpParams params = SystemDumpParams.create().
            dumpName( "dump" ).
            includeBinaries( true ).
            includeVersions( true ).
            maxAge( 10 ).
            maxVersions( 20 ).
            listener( new SystemDumpListenerImpl( progressReporter ) ).
            build();

        Mockito.when( this.dumpService.dump( Mockito.isA( SystemDumpParams.class ) ) ).thenReturn( systemDumpResult );

        final DumpRunnableTask task = createAndRunTask(
            new SystemDumpRequestJson( params.getDumpName(), params.isIncludeVersions(), params.getMaxAge(), params.getMaxVersions() ) );

        task.createTaskResult();

        Mockito.verify( progressReporter, Mockito.times( 1 ) ).info( contentQueryArgumentCaptor.capture() );
        Mockito.verify( taskService, Mockito.times( 1 ) ).submitTask( Mockito.isA( RunnableTask.class ), Mockito.eq( "dump" ) );

        final String result = contentQueryArgumentCaptor.getAllValues().get( 0 );
        jsonTestHelper.assertJsonEquals( jsonTestHelper.loadTestJson( "dump_result.json" ), jsonTestHelper.stringToJson( result ) );
    }

}
