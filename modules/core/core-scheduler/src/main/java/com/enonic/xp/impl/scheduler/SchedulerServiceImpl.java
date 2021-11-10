package com.enonic.xp.impl.scheduler;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.enonic.xp.cluster.ClusterConfig;
import com.enonic.xp.core.internal.concurrent.DynamicReference;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.scheduler.CreateScheduledJobParams;
import com.enonic.xp.scheduler.ModifyScheduledJobParams;
import com.enonic.xp.scheduler.ScheduledJob;
import com.enonic.xp.scheduler.ScheduledJobName;
import com.enonic.xp.scheduler.SchedulerService;

public class SchedulerServiceImpl
    implements SchedulerService
{
    private final IndexService indexService;

    private final RepositoryService repositoryService;

    private final SchedulerExecutorService schedulerExecutorService;

    private final ScheduleAuditLogSupport auditLogSupport;

    private final SchedulerJobManager localJobManager;

    private final DynamicReference<SchedulerJobManager> clusteredJobManagerRef;

    private final boolean clusterEnabled;

    public SchedulerServiceImpl( final IndexService indexService, final RepositoryService repositoryService,
                                 final SchedulerExecutorService schedulerExecutorService, final ScheduleAuditLogSupport auditLogSupport,
                                 final SchedulerJobManager localJobManager,
                                 final DynamicReference<SchedulerJobManager> clusteredJobManagerRef, final ClusterConfig config )
    {
        this.indexService = indexService;
        this.repositoryService = repositoryService;
        this.schedulerExecutorService = schedulerExecutorService;
        this.auditLogSupport = auditLogSupport;
        this.localJobManager = localJobManager;
        this.clusteredJobManagerRef = clusteredJobManagerRef;
        this.clusterEnabled = config.isEnabled();
    }

    public void initialize()
    {
        SchedulerRepoInitializer.create().setIndexService( indexService ).setRepositoryService( repositoryService ).build().initialize();
    }

    @Override
    public ScheduledJob create( final CreateScheduledJobParams params )
    {
        final ScheduledJob job = getJobManager().create( params );

        auditLogSupport.create( params, job );

        return job;
    }

    @Override
    public ScheduledJob modify( final ModifyScheduledJobParams params )
    {
        UnscheduleJobCommand.create().schedulerExecutorService( schedulerExecutorService ).name( params.getName() ).build().execute();

        final ScheduledJob job = getJobManager().modify( params );

        auditLogSupport.modify( params, job );

        return job;
    }

    @Override
    public boolean delete( final ScheduledJobName name )
    {
        UnscheduleJobCommand.create().schedulerExecutorService( schedulerExecutorService ).name( name ).build().execute();

        final boolean result = getJobManager().delete( name );

        auditLogSupport.delete( name, result );

        return result;
    }

    @Override
    public ScheduledJob get( final ScheduledJobName name )
    {
        return getJobManager().get( name );
    }

    @Override
    public List<ScheduledJob> list()
    {
        return getJobManager().list();
    }

    private SchedulerJobManager getJobManager()
    {
        if ( clusterEnabled )
        {
            try
            {
                return clusteredJobManagerRef.get( 5, TimeUnit.SECONDS );
            }
            catch ( InterruptedException | TimeoutException e )
            {
                throw new RuntimeException( e );
            }
        }
        return localJobManager;
    }
}
