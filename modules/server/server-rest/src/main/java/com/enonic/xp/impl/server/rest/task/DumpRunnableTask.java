package com.enonic.xp.impl.server.rest.task;

import com.enonic.xp.dump.DumpService;
import com.enonic.xp.dump.SystemDumpParams;
import com.enonic.xp.dump.SystemDumpResult;
import com.enonic.xp.impl.server.rest.model.SystemDumpRequestJson;
import com.enonic.xp.impl.server.rest.model.SystemDumpResultJson;
import com.enonic.xp.impl.server.rest.task.listener.SystemDumpListenerImpl;
import com.enonic.xp.task.AbstractRunnableTask;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskId;

public class DumpRunnableTask
    extends AbstractRunnableTask
{
    private final SystemDumpRequestJson params;

    private final DumpService dumpService;

    private DumpRunnableTask( Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.dumpService = builder.dumpService;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public void run( final TaskId id, final ProgressReporter progressReporter )
    {
        final SystemDumpParams systemDumpParams = SystemDumpParams.create().
            dumpName( params.getName() ).
            includeBinaries( true ).
            includeVersions( params.isIncludeVersions() ).
            maxAge( params.getMaxAge() ).
            maxVersions( params.getMaxVersions() ).
            listener( new SystemDumpListenerImpl( progressReporter ) ).
            build();

        final SystemDumpResult result = this.dumpService.dump( systemDumpParams );
        progressReporter.info( SystemDumpResultJson.from( result ).toString() );
    }

    public static class Builder
        extends AbstractRunnableTask.Builder<Builder>
    {
        private SystemDumpRequestJson params;

        private DumpService dumpService;

        public Builder params( SystemDumpRequestJson params )
        {
            this.params = params;
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
