package com.enonic.xp.repo.impl.vacuum;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.node.DeleteSnapshotParams;
import com.enonic.xp.repo.impl.SecurityHelper;
import com.enonic.xp.snapshot.SnapshotService;
import com.enonic.xp.vacuum.VacuumParameters;
import com.enonic.xp.vacuum.VacuumResult;
import com.enonic.xp.vacuum.VacuumService;
import com.enonic.xp.vacuum.VacuumTaskResult;

@Component(immediate = true, configurationPid = "com.enonic.xp.vacuum")
public class VacuumServiceImpl
    implements VacuumService
{
    private static final Logger LOG = LoggerFactory.getLogger( VacuumServiceImpl.class );

    private final VacuumTasks tasks = new VacuumTasks();

    private final SnapshotService snapshotService;

    private VacuumConfig config;

    @Activate
    public VacuumServiceImpl( @Reference final SnapshotService snapshotService )
    {
        this.snapshotService = snapshotService;
    }

    @Activate
    public void activate( final VacuumConfig config )
    {
        this.config = config;
    }

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
        final Instant vacuumStartedAt = Instant.now();

        //Retrieves the tasks to execute
        VacuumTasks tasks = getTasks( params );
        LOG.info( "Starting vacuum. Running " + tasks.size() + " tasks..." );
        if ( params.getVacuumListener() != null )
        {
            params.getVacuumListener().vacuumBegin( tasks.size() );
        }

        final long ageThreshold = getAgeThresholdMs( params );

        final VacuumResult.Builder taskResults = VacuumResult.create();
        for ( final VacuumTask task : tasks )
        {
            LOG.info( "Running vacuum task [" + task.name() + "]..." );

            final VacuumTaskParams taskParams = VacuumTaskParams.create()
                .listener( params.getVacuumListener() )
                .ageThreshold( ageThreshold )
                .versionsBatchSize( config.versionsBatchSize() )
                .vacuumStartedAt( vacuumStartedAt )
                .build();
            final VacuumTaskResult taskResult = task.execute( taskParams );

            LOG.info( task.name() + " : " + taskResult.toString() );
            taskResults.add( taskResult );
            LOG.info( "Vacuum task [" + task.name() + "] done" );
        }

        deleteSnapshotsIfNecessary( vacuumStartedAt );

        LOG.info( "Vacuum done" );

        return taskResults.build();
    }

    private void deleteSnapshotsIfNecessary( final Instant startedAt )
    {
        if ( tasks.hasTaskByName( "SegmentVacuumTask" ) || tasks.hasTaskByName( "BinaryBlobVacuumTask" ) ||
            tasks.hasTaskByName( "NodeBlobVacuumTask" ) )
        {
            try
            {
                snapshotService.delete( DeleteSnapshotParams.create().before( startedAt ).build() );
            }
            catch ( Exception e )
            {
                LOG.error( "Failed to delete snapshots", e );
            }
        }
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

    private long getAgeThresholdMs( final VacuumParameters params )
    {
        final Duration ageThreshold = params.getAgeThreshold();
        if ( ageThreshold != null )
        {
            return ageThreshold.toMillis();
        }
        return Duration.parse( config.ageThreshold() ).toMillis();
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
