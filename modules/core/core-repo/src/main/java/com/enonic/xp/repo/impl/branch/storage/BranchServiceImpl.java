package com.enonic.xp.repo.impl.branch.storage;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.node.NodeAlreadyExistAtPathException;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.NodeBranchEntries;
import com.enonic.xp.repo.impl.NodeBranchEntry;
import com.enonic.xp.repo.impl.branch.BranchService;
import com.enonic.xp.repo.impl.cache.BranchPath;
import com.enonic.xp.repo.impl.storage.SearchPreferences;
import com.enonic.xp.repo.impl.version.NodeVersionFactory;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.storage.spi.BranchEntryRecord;
import com.enonic.xp.storage.spi.NodeSegments;
import com.enonic.xp.storage.spi.NodeStore;

@Component
public class BranchServiceImpl
    implements BranchService
{
    private final Cache<BranchPath, NodeBranchEntry> cache =
        CacheBuilder.newBuilder().maximumSize( 100000 ).expireAfterWrite( Duration.ofMinutes( 10 ) ).build();

    private final NodeStore nodeStore;

    @Activate
    public BranchServiceImpl( @Reference final NodeStore nodeStore )
    {
        this.nodeStore = nodeStore;
    }

    @Override
    public void push( final NodeBranchEntry nodeBranchEntry, final InternalContext context )
    {
        final RepositoryId repositoryId = context.getRepositoryId();
        final Branch branch = context.getBranch();

        cache.asMap().compute( new BranchPath( repositoryId, branch, nodeBranchEntry.getNodePath() ), ( cK, inCache ) -> {
            this.nodeStore.storeBranchEntry( cK.getRepositoryId(), cK.getBranch(), NodeBranchVersionFactory.toRecord( nodeBranchEntry ) );
            return nodeBranchEntry;
        } );
    }

    @Override
    public void store( final NodeBranchEntry nodeBranchEntry, final InternalContext context )
    {
        final RepositoryId repositoryId = context.getRepositoryId();
        final Branch branch = context.getBranch();

        cache.asMap().compute( new BranchPath( repositoryId, branch, nodeBranchEntry.getNodePath() ), ( cK, inCache ) -> {
            if ( inCache != null && !inCache.getNodeId().equals( nodeBranchEntry.getNodeId() ) )
            {
                throw new NodeAlreadyExistAtPathException( nodeBranchEntry.getNodePath(), repositoryId, branch );
            }

            this.nodeStore.storeBranchEntry( cK.getRepositoryId(), cK.getBranch(), NodeBranchVersionFactory.toRecord( nodeBranchEntry ) );
            return nodeBranchEntry;
        } );
    }

    @Override
    public void storeWithVersion( final NodeBranchEntry nodeBranchEntry, final NodeVersion nodeVersion, final NodeSegments segments,
                                   final InternalContext context )
    {
        final RepositoryId repositoryId = context.getRepositoryId();
        final Branch branch = context.getBranch();

        cache.asMap().compute( new BranchPath( repositoryId, branch, nodeBranchEntry.getNodePath() ), ( cK, inCache ) -> {
            if ( inCache != null && !inCache.getNodeId().equals( nodeBranchEntry.getNodeId() ) )
            {
                throw new NodeAlreadyExistAtPathException( nodeBranchEntry.getNodePath(), repositoryId, branch );
            }

            this.nodeStore.storeNode( cK.getRepositoryId(), cK.getBranch(), segments, NodeVersionFactory.toRecord( nodeVersion ),
                                       NodeBranchVersionFactory.toRecord( nodeBranchEntry ) );
            return nodeBranchEntry;
        } );
    }

    @Override
    public void delete( final Collection<NodeBranchEntry> entries, final InternalContext context )
    {
        final RepositoryId repositoryId = context.getRepositoryId();
        final Branch branch = context.getBranch();

        try
        {
            nodeStore.deleteBranchEntries( repositoryId, branch,
                                            entries.stream().map( entry -> entry.getNodeId().toString() ).collect( Collectors.toList() ) );
        }
        finally
        {
            cache.invalidateAll(
                entries.stream().map( nbe -> new BranchPath( repositoryId, branch, nbe.getNodePath() ) ).collect( Collectors.toList() ) );
        }
    }

    @Override
    public NodeBranchEntry get( final NodeId nodeId, final InternalContext context )
    {
        return doGetById( nodeId, context );
    }

    @Override
    public boolean exists( final NodeId nodeId, final InternalContext context )
    {
        return nodeStore.existsBranchEntry( context.getRepositoryId(), context.getBranch(), nodeId.toString(),
                                             SearchPreferences.toSpi( context.getSearchPreference() ) );
    }

    private NodeBranchEntry doGetById( final NodeId nodeId, final InternalContext context )
    {
        final BranchEntryRecord record = nodeStore.getBranchEntry( context.getRepositoryId(), context.getBranch(), nodeId.toString(),
                                                                    SearchPreferences.toSpi( context.getSearchPreference() ) );

        return record == null ? null : NodeBranchVersionFactory.fromRecord( record );
    }

    @Override
    public NodeBranchEntries get( final Iterable<NodeId> nodeIds, final InternalContext context )
    {
        final List<String> ids = new ArrayList<>();
        nodeIds.forEach( nodeId -> ids.add( nodeId.toString() ) );

        final List<BranchEntryRecord> records = nodeStore.getBranchEntries( context.getRepositoryId(), context.getBranch(), ids,
                                                                              SearchPreferences.toSpi( context.getSearchPreference() ) );

        final NodeBranchEntries.Builder builder = NodeBranchEntries.create();
        records.stream().map( NodeBranchVersionFactory::fromRecord ).forEach( builder::add );
        return builder.build();
    }

    @Override
    public NodeBranchEntry get( final NodePath nodePath, final InternalContext context )
    {
        final RepositoryId repositoryId = context.getRepositoryId();
        final Branch branch = context.getBranch();
        final BranchPath cacheKey = new BranchPath( repositoryId, branch, nodePath );

        return cache.asMap().compute( cacheKey, ( cK, inCache ) -> {
            if ( inCache != null )
            {
                final NodeBranchEntry nodeBranchEntry = doGetById( inCache.getNodeId(), context );
                if ( nodeBranchEntry != null && nodeBranchEntry.getNodePath().equals( nodePath ) )
                {
                    return nodeBranchEntry;
                }
            }

            final BranchEntryRecord record = nodeStore.getBranchEntryByPath( repositoryId, branch, nodePath.toString(),
                                                                              SearchPreferences.toSpi( context.getSearchPreference() ) );

            return record == null ? null : NodeBranchVersionFactory.fromRecord( record );
        } );
    }

    @Override
    public void evictPath( final NodePath nodePath, final InternalContext context )
    {
        cache.invalidate( new BranchPath( context.getRepositoryId(), context.getBranch(), nodePath ) );
    }

    @Override
    public void evictAllPaths()
    {
        cache.invalidateAll();
    }

    @Override
    public Branches getBranches( NodeId nodeId, RepositoryId repositoryId )
    {
        return nodeStore.getBranchesWithNode( repositoryId, nodeId.toString() ).stream().collect( Branches.collector() );
    }
}
