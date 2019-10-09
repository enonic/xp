package com.enonic.xp.impl.server.rest.task;

import com.enonic.xp.impl.server.rest.model.VacuumRequestJson;
import com.enonic.xp.impl.server.rest.model.VacuumResultJson;
import com.enonic.xp.impl.server.rest.task.listener.VacuumListenerImpl;
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

    private VacuumRequestJson params;

    private VacuumRunnableTask( Builder builder )
    {
        super( builder );
        this.vacuumService = builder.vacuumService;
        this.params = builder.params == null ? new VacuumRequestJson( null, null ) : builder.params;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public void run( final TaskId id, final ProgressReporter progressReporter )
    {
        final VacuumParameters vacuumParams = VacuumParameters.create().
            vacuumListener( new VacuumListenerImpl( progressReporter ) ).
            ageThreshold( params.getAgeThreshold() ).
            taskConfigMap( params.getTaskConfigMap() ).
            build();

        final VacuumResult result = this.vacuumService.vacuum( vacuumParams );
        progressReporter.info( VacuumResultJson.from( result ).toString() );

    }

    public static class Builder
        extends AbstractRunnableTask.Builder<Builder>
    {
        private VacuumService vacuumService;

        private VacuumRequestJson params;

        public Builder vacuumService( final VacuumService vacuumService )
        {
            this.vacuumService = vacuumService;
            return this;
        }

        public Builder params( final VacuumRequestJson params )
        {
            this.params = params;
            return this;
        }

        public VacuumRunnableTask build()
        {
            return new VacuumRunnableTask( this );
        }
    }
}
