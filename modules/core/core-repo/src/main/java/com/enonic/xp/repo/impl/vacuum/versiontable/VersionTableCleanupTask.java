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
import com.enonic.xp.node.NodeVersionQueryResult;
import com.enonic.xp.query.filter.RangeFilter;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.search.result.SearchResult;
import com.enonic.xp.repo.impl.vacuum.AbstractVacuumTask;
import com.enonic.xp.repo.impl.vacuum.VacuumTask;
import com.enonic.xp.repo.impl.vacuum.VacuumTaskParams;
import com.enonic.xp.repo.impl.version.NodeVersionDocumentId;
import com.enonic.xp.repo.impl.version.VersionIndexPath;
import com.enonic.xp.repo.impl.version.VersionService;
import com.enonic.xp.repo.impl.version.search.NodeVersionQueryResultFactory;
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

    private VacuumListener listener;

    private VacuumTaskResult.Builder result;

    private Instant until;

    private boolean repositoryCleanStarted;

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
        this.result = VacuumTaskResult.create().taskName( this.name() );
        this.listener = params.getListener();
        this.until = Instant.now().minusMillis( params.getAgeThreshold() );

        this.repositoryService.list().forEach( this::cleanRepository );

        return result.build();
    }

    private void cleanRepository( final Repository repository )
    {
        ContextBuilder.from( ContextAccessor.current() ).
            repositoryId( repository.getId() ).
            branch( RepositoryConstants.MASTER_BRANCH ).
            build().
            runWith( () -> doCleanRepository( repository ) );
    }

    private void doCleanRepository( final Repository repository )
    {
        this.repositoryCleanStarted = false;
        final RepositoryId repositoryId = repository.getId();
        LOG.info( "Cleaning repository: " + repositoryId );

        NodeVersionQuery query = createQuery( repository );
        nodeService.findVersions( query );
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

    private NodeVersionQuery createQuery( final Repository repository )
    {
        final NodeVersionQuery.Builder builder = NodeVersionQuery.create();

        builder.addQueryFilter( RangeFilter.create().
            fieldName( VersionIndexPath.TIMESTAMP.getPath() ).
            to( ValueFactory.newDateTime( this.until ) ).
            build() );

        builder.size( -1 ).
            batchSize( 100 ).
            batchCallback( result -> this.handleBatchCallback( (SearchResult) result, repository, builder.build() ) );

        return builder.build();
    }

    private void handleBatchCallback( final SearchResult searchResult, final Repository repository,
                                      final NodeVersionQuery nodeVersionQuery )
    {
        final List<NodeVersionDocumentId> toBeDeleted = Lists.newArrayList();

        final long versionTotal = searchResult.getTotalHits();
        if ( !this.repositoryCleanStarted )
        {
            if ( listener != null )
            {
                listener.vacuumingVersionRepository( repository.getId(), versionTotal );

            }
            this.repositoryCleanStarted = true;
        }

        if ( searchResult.isEmpty() )
        {
            return;
        }

        final NodeVersionQueryResult nodeVersionQueryResult = NodeVersionQueryResultFactory.create( nodeVersionQuery, searchResult );

        nodeVersionQueryResult.getNodeVersionsMetadata().forEach( ( version ) -> {
            processVersion( repository, result, toBeDeleted, version );
            if ( listener != null )
            {
                listener.vacuumingVersion( 1L );
            }
        } );

        LOG.info( "Deleting: " + toBeDeleted.size() + " versions from repository: " + repository.getId() );

        versionService.delete( toBeDeleted, InternalContext.from( ContextAccessor.current() ) );
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
