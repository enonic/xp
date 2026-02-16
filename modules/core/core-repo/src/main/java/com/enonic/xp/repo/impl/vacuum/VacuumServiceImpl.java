package com.enonic.xp.repo.impl.vacuum;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.core.internal.concurrent.AtomicSortedList;
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

    private final AtomicSortedList<VacuumTask> allTasks = new AtomicSortedList<>( Comparator.comparingInt( VacuumTask::order ) );

    private final SnapshotService snapshotService;

    private volatile VacuumConfig config;

    @Activate
    public VacuumServiceImpl( @Reference final SnapshotService snapshotService )
    {
        this.snapshotService = snapshotService;
    }

    @Activate
    @Modified
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

        final List<VacuumTask> tasks = getTasksToExecute( params );
        LOG.info( "Starting vacuum. Running {} tasks...", tasks.stream().map( VacuumTask::name ).collect( Collectors.toList() ) );

        if ( params.getVacuumListener() != null )
        {
            params.getVacuumListener().vacuumBegin( tasks.size() );
        }

        final long ageThreshold =
            Objects.requireNonNullElseGet( params.getAgeThreshold(), () -> Duration.parse( config.ageThreshold() ) ).toMillis();

        final VacuumResult.Builder taskResults = VacuumResult.create();
        for ( final VacuumTask task : tasks )
        {
            LOG.info( "Running vacuum task [{}]...", task.name() );

            final VacuumTaskParams taskParams = VacuumTaskParams.create()
                .listener( params.getVacuumListener() )
                .ageThreshold( ageThreshold )
                .versionsBatchSize( config.versionsBatchSize() )
                .vacuumStartedAt( vacuumStartedAt )
                .build();
            final VacuumTaskResult taskResult = task.execute( taskParams );

            LOG.info( "Vacuum task [{}] completed: {}", task.name(), taskResult );
            taskResults.add( taskResult );
        }

        if ( tasks.stream().anyMatch( VacuumTask::deletesBlobs ) )
        {
            LOG.info( "Deleting all snapshots because they are obsolete" );
            try
            {
                snapshotService.delete( DeleteSnapshotParams.create().before( Instant.now() ).build() );
            }
            catch ( Exception e )
            {
                LOG.error( "Failed to delete snapshots", e );
            }
        }

        LOG.info( "Vacuum done" );

        return taskResults.build();
    }

    private List<VacuumTask> getTasksToExecute( final VacuumParameters params )
    {
        final List<VacuumTask> allTasksSnapshot = this.allTasks.snapshot();
        final Set<String> taskNames = params.getTaskNames();
        return taskNames == null
            ? allTasksSnapshot
            : allTasksSnapshot.stream().filter( t -> taskNames.contains( t.name() ) ).collect( Collectors.toUnmodifiableList() );
    }

    @SuppressWarnings("WeakerAccess")
    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addTask( final VacuumTask task )
    {
        this.allTasks.add( task );
    }

    public void removeTask( final VacuumTask task )
    {
        this.allTasks.remove( task );
    }
}
