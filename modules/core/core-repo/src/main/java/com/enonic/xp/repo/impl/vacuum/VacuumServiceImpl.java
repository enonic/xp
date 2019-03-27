package com.enonic.xp.repo.impl.vacuum;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.repo.impl.SecurityHelper;
import com.enonic.xp.vacuum.VacuumParameters;
import com.enonic.xp.vacuum.VacuumResult;
import com.enonic.xp.vacuum.VacuumService;
import com.enonic.xp.vacuum.VacuumTaskResult;

@Component(immediate = true)
public class VacuumServiceImpl
    implements VacuumService
{
    private final VacuumTasks tasks = new VacuumTasks();

    private final static Logger LOG = LoggerFactory.getLogger( VacuumServiceImpl.class );

    @Override
    public VacuumResult vacuum( final VacuumParameters params )
    {
        if ( !SecurityHelper.isAdmin() )
        {
            throw new VacuumException( "Only admin role users can execute vacuum" );
        }

        LOG.info( " Starting vacuum, running " + tasks.size() + " tasks" );

        final VacuumResult.Builder taskResults = doVacuum( params );

        return taskResults.build();
    }

    private VacuumResult.Builder doVacuum( final VacuumParameters params )
    {
        final VacuumResult.Builder taskResults = VacuumResult.create();

        final VacuumTaskParams taskParams = VacuumTaskParams.create().listener( params.getVacuumProgressListener() ).build();

        if ( params.getVacuumTaskListener() != null )
        {
            params.getVacuumTaskListener().total( tasks.size() );
        }

        for ( final VacuumTask task : this.tasks )
        {
            LOG.info( "Running VacuumTask:" + task.name() );
            final VacuumTaskResult taskResult = task.execute( taskParams );
            LOG.info( task.name() + " : " + taskResult.toString() );
            taskResults.add( taskResult );
            LOG.info( "VacuumTask done: " + task.name() );

            if ( params.getVacuumTaskListener() != null )
            {
                params.getVacuumTaskListener().taskExecuted();
            }
        }
        return taskResults;
    }

    @SuppressWarnings("WeakerAccess")
    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addTask( final VacuumTask task )
    {
        this.tasks.add( task );
    }

    public void removeTask( final VacuumTask task )
    {
        this.tasks.remove( task );
    }
}
