package com.enonic.xp.impl.server.rest.task;

import com.enonic.xp.dump.DumpService;
import com.enonic.xp.dump.SystemLoadListener;
import com.enonic.xp.dump.SystemLoadParams;
import com.enonic.xp.dump.SystemLoadResult;
import com.enonic.xp.impl.server.rest.model.SystemLoadResultJson;
import com.enonic.xp.impl.server.rest.task.listener.SystemLoadListenerImpl;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;

public class LoadRunnableTask
    implements RunnableTask
{
    private final String name;

    private final boolean upgrade;

    private final boolean archive;

    private final TaskService taskService;

    private final DumpService dumpService;

    private SystemLoadListener loadDumpListener;

    private LoadRunnableTask( Builder builder )
    {
        this.name = builder.name;
        this.upgrade = builder.upgrade;
        this.archive = builder.archive;
        this.taskService = builder.taskService;
        this.dumpService = builder.dumpService;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public void run( final TaskId id, final ProgressReporter progressReporter )
    {
        TaskUtils.checkAlreadySubmitted( taskService.getTaskInfo( id ), taskService.getAllTasks() );

        loadDumpListener = new SystemLoadListenerImpl( progressReporter );

        final SystemLoadResultJson result;

        result = doLoadFromSystemDump();

        progressReporter.info( result.toString() );
    }


    private SystemLoadResultJson doLoadFromSystemDump()
    {
        final SystemLoadResult systemLoadResult = this.dumpService.load( SystemLoadParams.create()
                                                                             .dumpName( name )
                                                                             .upgrade( upgrade )
                                                                             .archive( archive )
                                                                             .includeVersions( true )
                                                                             .listener( loadDumpListener )
                                                                             .build() );

        return SystemLoadResultJson.from( systemLoadResult );
    }

    public static class Builder
    {
        private String name;

        private boolean upgrade;

        private boolean archive;

        private DumpService dumpService;

        private TaskService taskService;

        public Builder taskService( final TaskService taskService )
        {
            this.taskService = taskService;
            return this;
        }

        public Builder name( String name )
        {
            this.name = name;
            return this;
        }

        public Builder upgrade( boolean upgrade )
        {
            this.upgrade = upgrade;
            return this;
        }

        public Builder archive( boolean archive )
        {
            this.archive = archive;
            return this;
        }

        public Builder dumpService( final DumpService dumpService )
        {
            this.dumpService = dumpService;
            return this;
        }

        public LoadRunnableTask build()
        {
            return new LoadRunnableTask( this );
        }
    }
}
