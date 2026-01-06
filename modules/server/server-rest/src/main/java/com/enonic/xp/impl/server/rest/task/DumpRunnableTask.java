package com.enonic.xp.impl.server.rest.task;

import com.enonic.xp.dump.DumpService;
import com.enonic.xp.dump.SystemDumpParams;
import com.enonic.xp.dump.SystemDumpResult;
import com.enonic.xp.impl.server.rest.model.SystemDumpResultJson;
import com.enonic.xp.impl.server.rest.task.listener.SystemDumpListenerImpl;
import com.enonic.xp.task.ProgressReportParams;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.SubmitLocalTaskParams;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;

public class DumpRunnableTask
    implements RunnableTask
{
    private final String name;

    private final boolean includeVersions;

    private final boolean archive;

    private final Integer maxAge;

    private final Integer maxVersions;

    private final DumpService dumpService;

    private final TaskService taskService;

    private DumpRunnableTask( Builder builder )
    {
        this.name = builder.name;
        this.includeVersions = builder.includeVersions;
        this.archive = builder.archive;
        this.maxAge = builder.maxAge;
        this.maxVersions = builder.maxVersions;
        this.dumpService = builder.dumpService;
        this.taskService = builder.taskService;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public TaskId execute()
    {
        return taskService.submitLocalTask( SubmitLocalTaskParams.create().runnableTask( this ).name( "dump" ).description( "Dump " + name ).build() );
    }

    @Override
    public void run( final TaskId id, final ProgressReporter progressReporter )
    {
        TaskUtils.checkAlreadySubmitted( taskService.getTaskInfo( id ), taskService.getAllTasks() );
        final SystemDumpParams systemDumpParams = SystemDumpParams.create()
            .dumpName( name )
            .includeBinaries( true )
            .includeVersions( includeVersions )
            .maxAge( maxAge )
            .archive( archive )
            .maxVersions( maxVersions )
            .listener( new SystemDumpListenerImpl( progressReporter ) )
            .build();

        final SystemDumpResult result = this.dumpService.dump( systemDumpParams );
        progressReporter.progress( ProgressReportParams.create( SystemDumpResultJson.from( result ).toString() ).build() );
    }

    public static class Builder
    {
        private String name;

        private boolean includeVersions;

        private boolean archive;

        private Integer maxAge;

        private Integer maxVersions;

        private DumpService dumpService;

        private TaskService taskService;

        public Builder taskService( final TaskService taskService )
        {
            this.taskService = taskService;
            return this;
        }

        public Builder name( final String name )
        {
            this.name = name;
            return this;
        }

        public Builder includeVersions( final boolean includeVersions )
        {
            this.includeVersions = includeVersions;
            return this;
        }

        public Builder archive( final boolean archive )
        {
            this.archive = archive;
            return this;
        }

        public Builder maxAge( final Integer maxAge )
        {
            this.maxAge = maxAge;
            return this;
        }

        public Builder maxVersions( final Integer maxVersions )
        {
            this.maxVersions = maxVersions;
            return this;
        }

        public Builder dumpService( final DumpService dumpService )
        {
            this.dumpService = dumpService;
            return this;
        }

        public DumpRunnableTask build()
        {
            return new DumpRunnableTask( this );
        }
    }
}
