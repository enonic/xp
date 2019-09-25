package com.enonic.xp.repo.impl.vacuum;

import java.util.Set;

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
        return doVacuum( params );
    }

    private VacuumResult doVacuum( final VacuumParameters params )
    {
        //Retrieves the tasks to execute
        VacuumTasks tasks = getTasks( params );
        LOG.info( "Starting vacuum. Running " + tasks.size() + " tasks..." );
        if ( params.getVacuumTaskListener() != null )
        {
            params.getVacuumTaskListener().total( tasks.size() );
        }

        final VacuumResult.Builder taskResults = VacuumResult.create();
        for ( final VacuumTask task : tasks )
        {
            LOG.info( "Running vacuum task [" + task.name() + "]" );

            final VacuumTaskParams taskParams = VacuumTaskParams.create().
                listener( params.getVacuumProgressListener() ).
                ageThreshold( params.getAgeThreshold() ).
                config( params.getTaskConfig( task.name() ) ).
                build();
            final VacuumTaskResult taskResult = task.execute( taskParams );

            LOG.info( task.name() + " : " + taskResult.toString() );
            taskResults.add( taskResult );
            LOG.info( "Vacuum task [" + task.name() + "] done");

            if ( params.getVacuumTaskListener() != null )
            {
                params.getVacuumTaskListener().taskExecuted();
            }
        }
        return taskResults.build();
    }

    private VacuumTasks getTasks( final VacuumParameters params )
    {
        final Set<String> taskNames = params.getTaskNames();
        if ( taskNames == null )
        {
            return this.tasks;
        }

        final VacuumTasks filteredTasks = new VacuumTasks();
        for ( VacuumTask task : this.tasks )
        {
            if ( taskNames.contains( task.name() ) )
            {
                filteredTasks.add( task );
            }
        }
        return filteredTasks;
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
