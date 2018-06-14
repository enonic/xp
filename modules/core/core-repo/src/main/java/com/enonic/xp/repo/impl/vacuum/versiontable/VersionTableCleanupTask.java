package com.enonic.xp.repo.impl.vacuum.versiontable;

import java.time.Instant;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.node.NodeVersionQuery;
import com.enonic.xp.node.NodeVersionsMetadata;
import com.enonic.xp.query.filter.RangeFilter;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.node.executor.BatchedGetVersionsExecutor;
import com.enonic.xp.repo.impl.vacuum.AbstractVacuumTask;
import com.enonic.xp.repo.impl.vacuum.VacuumTask;
import com.enonic.xp.repo.impl.vacuum.VacuumTaskParams;
import com.enonic.xp.repo.impl.version.NodeVersionDocumentId;
import com.enonic.xp.repo.impl.version.VersionIndexPath;
import com.enonic.xp.repo.impl.version.VersionService;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryConstants;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.vacuum.VacuumListener;
import com.enonic.xp.vacuum.VacuumTaskResult;

@Component(immediate = true)
public class VersionTableCleanupTask
    extends AbstractVacuumTask
    implements VacuumTask
{
    private NodeService nodeService;

    private RepositoryService repositoryService;

    private VersionService versionService;

    private final static Logger LOG = LoggerFactory.getLogger( VersionTableCleanupTask.class );

    @Override
    public int order()
    {
        return 300;
    }

    @Override
    public String name()
    {
        return "UnusedVersionTableEntryCleaner";
    }

    public VacuumTaskResult execute( final VacuumTaskParams params )
    {
        final NodeVersionQuery query = createQuery( params );
        final VacuumTaskResult.Builder result = VacuumTaskResult.create().taskName( this.name() );

        this.repositoryService.list().forEach( repo -> cleanRepository( repo, query, result, params.getListener() ) );

        return result.build();
    }

    private void cleanRepository( final Repository repository, final NodeVersionQuery query, final VacuumTaskResult.Builder result,
                                  final VacuumListener listener )
    {
        ContextBuilder.from( ContextAccessor.current() ).
            repositoryId( repository.getId() ).
            branch( RepositoryConstants.MASTER_BRANCH ).
            build().
            runWith( () -> doCleanRepository( repository, query, result, listener ) );
    }

    private void doCleanRepository( final Repository repository, final NodeVersionQuery query, final VacuumTaskResult.Builder result,
                                    final VacuumListener listener )
    {
        final RepositoryId repositoryId = repository.getId();
        LOG.info( "Cleaning repository: " + repositoryId );

        final BatchedGetVersionsExecutor executor = BatchedGetVersionsExecutor.create().
            query( query ).
            nodeService( this.nodeService ).
            build();

        final List<NodeVersionDocumentId> toBeDeleted = Lists.newArrayList();

        if ( listener != null )
        {
            final Long versionTotal = executor.getTotalHits();
            listener.vacuumingVersionRepository( repositoryId, versionTotal );
        }

        while ( executor.hasMore() )
        {
            final NodeVersionsMetadata versions = executor.execute();
            versions.forEach( ( version ) -> {
                processVersion( repository, result, toBeDeleted, version );
                if ( listener != null )
                {
                    listener.vacuumingVersion( 1L );
                }
            } );
        }

        LOG.info( "Deleting: " + toBeDeleted.size() + " versions from repository: " + repositoryId );

        versionService.delete( toBeDeleted, InternalContext.from( ContextAccessor.current() ) );
    }

    private void processVersion( final Repository repository, final VacuumTaskResult.Builder result,
                                 final List<NodeVersionDocumentId> toBeDeleted, final NodeVersionMetadata version )
    {
        result.processed();

        final boolean versionInUse;
        try
        {
            versionInUse = versionUsedInAnyBranch( repository, version );
            if ( !versionInUse )
            {
                result.deleted();
                toBeDeleted.add( new NodeVersionDocumentId( version.getNodeId(), version.getNodeVersionId() ) );
            }
            else
            {
                result.inUse();
            }
        }
        catch ( Exception e )
        {
            result.failed();
            LOG.error( String.format( "Cannot verify version with id %s in repository %s", version.getNodeVersionId(), repository.getId() ),
                       e );
        }
    }

    private boolean versionUsedInAnyBranch( final Repository repository, final NodeVersionMetadata version )
    {
        for ( final Branch branch : repository.getBranches() )
        {
            try
            {
                ContextBuilder.from( ContextAccessor.current() ).
                    branch( branch ).
                    repositoryId( repository.getId() ).
                    build().callWith( () -> this.nodeService.getById( version.getNodeId() ) );
                return true;
            }
            catch ( NodeNotFoundException e )
            {
                // Ignore
            }
        }

        return false;
    }

    private NodeVersionQuery createQuery( final VacuumTaskParams params )
    {
        final Instant since = Instant.now().minusMillis( params.getAgeThreshold() );

        return NodeVersionQuery.create().
            addQueryFilter( RangeFilter.create().
                fieldName( VersionIndexPath.TIMESTAMP.getPath() ).
                to( ValueFactory.newDateTime( since ) ).
                build() ).
            build();
    }

    @Reference
    public void setNodeService( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }

    @Reference
    public void setRepositoryService( final RepositoryService repositoryService )
    {
        this.repositoryService = repositoryService;
    }

    @Reference
    public void setVersionService( final VersionService versionService )
    {
        this.versionService = versionService;
    }
}
