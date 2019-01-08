package com.enonic.xp.impl.server.rest.task;

import com.enonic.xp.impl.server.rest.VacuumProgressLogger;
import com.enonic.xp.impl.server.rest.model.VacuumResultJson;
import com.enonic.xp.impl.server.rest.task.listener.VacuumTaskListenerImpl;
import com.enonic.xp.task.AbstractRunnableTask;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.vacuum.VacuumParameters;
import com.enonic.xp.vacuum.VacuumResult;
import com.enonic.xp.vacuum.VacuumService;

public class VacuumRunnableTask
    extends AbstractRunnableTask
{
    private final VacuumService vacuumService;

    private VacuumRunnableTask( Builder builder )
    {
        super( builder );
        this.vacuumService = builder.vacuumService;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public void run( final TaskId id, final ProgressReporter progressReporter )
    {
        final VacuumParameters vacuumParams = VacuumParameters.create().
            vacuumProgressListener( new VacuumProgressLogger() ).
            vacuumTaskListener( new VacuumTaskListenerImpl( progressReporter ) ).
            build();

        final VacuumResult result = this.vacuumService.vacuum( vacuumParams );
        progressReporter.info( VacuumResultJson.from( result ).toString() );

    }

    public static class Builder
        extends AbstractRunnableTask.Builder<Builder>
    {
        private VacuumService vacuumService;

        public Builder vacuumService( final VacuumService vacuumService )
        {
            this.vacuumService = vacuumService;
            return this;
        }

        public VacuumRunnableTask build()
        {
            return new VacuumRunnableTask( this );
        }
    }
}
