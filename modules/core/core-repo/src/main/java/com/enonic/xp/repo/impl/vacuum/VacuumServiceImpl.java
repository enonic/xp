package com.enonic.xp.repo.impl.vacuum;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.vacuum.VacuumParameters;
import com.enonic.xp.vacuum.VacuumResult;
import com.enonic.xp.vacuum.VacuumService;

@Component(immediate = true)
public class VacuumServiceImpl
    implements VacuumService
{
    private final VacuumTasks tasks = new VacuumTasks();

    private final static Logger LOG = LoggerFactory.getLogger( VacuumServiceImpl.class );

    @Override
    public VacuumResult vacuum( final VacuumParameters params )
    {
        LOG.info( " Starting vacuum" );

        final VacuumResult.Builder taskResults = VacuumResult.create();

        for ( final VacuumTask task : this.tasks )
        {
            taskResults.add( task.execute( new VacuumTaskParams() ) );
        }

        return taskResults.build();
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addTask( final VacuumTask task )
    {
        this.tasks.add( task );
    }
}
