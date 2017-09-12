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

        final VacuumResult.Builder taskResults = doVacuum();

        return taskResults.build();
    }

    private VacuumResult.Builder doVacuum()
    {
        final VacuumResult.Builder taskResults = VacuumResult.create();

        for ( final VacuumTask task : this.tasks )
        {
            LOG.info( "Running VacuumTask:" + this.getClass().getName() );
            final VacuumTaskResult taskResult = task.execute( VacuumTaskParams.create().build() );
            LOG.info( task.name() + " : " + taskResult.toString() );
            taskResults.add( taskResult );
            LOG.info( "VacuumTask done: " + this.getClass().getName() );
        }
        return taskResults;
    }

    @SuppressWarnings("WeakerAccess")
    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addTask( final VacuumTask task )
    {
        this.tasks.add( task );
    }
}
