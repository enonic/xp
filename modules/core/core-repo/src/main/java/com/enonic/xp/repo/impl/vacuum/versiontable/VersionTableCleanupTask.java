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
import com.enonic.xp.repository.RepositoryService;
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
        final VacuumTaskResult.Builder result = VacuumTaskResult.create();

        this.repositoryService.list().forEach( repo -> cleanRepository( repo, query, result ) );

        return result.build();
    }

    private void cleanRepository( final Repository repository, final NodeVersionQuery query, final VacuumTaskResult.Builder result )
    {
        ContextBuilder.from( ContextAccessor.current() ).
            repositoryId( repository.getId() ).
            branch( RepositoryConstants.MASTER_BRANCH ).
            build().
            runWith( () -> doCleanRepository( repository, query, result ) );
    }

    private void doCleanRepository( final Repository repository, final NodeVersionQuery query, final VacuumTaskResult.Builder result )
    {
        LOG.info( "Cleaning repository: " + repository.getId() );

        final BatchedGetVersionsExecutor executor = BatchedGetVersionsExecutor.create().
            query( query ).
            nodeService( this.nodeService ).
            build();

        final List<NodeVersionDocumentId> toBeDeleted = Lists.newArrayList();

        while ( executor.hasMore() )
        {
            final NodeVersionsMetadata versions = executor.execute();

            versions.forEach( ( version ) -> {

                result.processed();

                if ( !versionUsedInAnyBranch( repository, version ) )
                {
                    result.deleted();
                    toBeDeleted.add( new NodeVersionDocumentId( version.getNodeId(), version.getNodeVersionId() ) );
                }

            } );
        }

        LOG.info( "Deleting: " + toBeDeleted.size() + " versions from repository: " + repository.getId() );

        versionService.delete( toBeDeleted, InternalContext.from( ContextAccessor.current() ) );
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
