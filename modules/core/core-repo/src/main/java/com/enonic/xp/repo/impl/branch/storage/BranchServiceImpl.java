package com.enonic.xp.repo.impl.branch.storage;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.node.NodeAlreadyExistAtPathException;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.NodeBranchEntries;
import com.enonic.xp.repo.impl.NodeBranchEntry;
import com.enonic.xp.repo.impl.ReturnFields;
import com.enonic.xp.repo.impl.SingleRepoStorageSource;
import com.enonic.xp.repo.impl.StorageSource;
import com.enonic.xp.repo.impl.branch.BranchService;
import com.enonic.xp.repo.impl.branch.search.NodeBranchQuery;
import com.enonic.xp.repo.impl.cache.BranchPath;
import com.enonic.xp.repo.impl.search.SearchDao;
import com.enonic.xp.repo.impl.search.SearchRequest;
import com.enonic.xp.repo.impl.search.result.SearchResult;
import com.enonic.xp.repo.impl.storage.DeleteRequests;
import com.enonic.xp.repo.impl.storage.GetByIdRequest;
import com.enonic.xp.repo.impl.storage.GetByIdsRequest;
import com.enonic.xp.repo.impl.storage.GetResult;
import com.enonic.xp.repo.impl.storage.StaticStorageType;
import com.enonic.xp.repo.impl.storage.StorageDao;
import com.enonic.xp.repo.impl.storage.StoreStorageName;
import com.enonic.xp.repository.RepositoryId;

@Component
public class BranchServiceImpl
    implements BranchService
{
    private static final ReturnFields BRANCH_RETURN_FIELDS = ReturnFields.from( BranchIndexPath.entryFields() );

    private final Cache<BranchPath, NodeBranchEntry> cache =
        CacheBuilder.newBuilder().maximumSize( 100000 ).expireAfterWrite( Duration.ofMinutes( 10 ) ).build();

    private final StorageDao storageDao;

    private final SearchDao searchDao;

    @Activate
    public BranchServiceImpl( @Reference final StorageDao storageDao, @Reference final SearchDao searchDao )
    {
        this.storageDao = storageDao;
        this.searchDao = searchDao;
    }

    @Override
    public void push( final NodeBranchEntry nodeBranchEntry, final InternalContext context )
    {
        final RepositoryId repositoryId = context.getRepositoryId();
        final Branch branch = context.getBranch();

        cache.asMap().compute( new BranchPath( repositoryId, branch, nodeBranchEntry.getNodePath() ), ( cK, inCache ) -> {
            this.storageDao.store( BranchStorageRequestFactory.create( nodeBranchEntry, cK.getRepositoryId(), cK.getBranch() ) );
            return nodeBranchEntry;
        } );
    }

    @Override
    public void store( final NodeBranchEntry nodeBranchEntry, final InternalContext context )
    {
        final RepositoryId repositoryId = context.getRepositoryId();
        final Branch branch = context.getBranch();

        final boolean[] alreadyExists = {false};
        cache.asMap().compute( new BranchPath( repositoryId, branch, nodeBranchEntry.getNodePath() ), ( cK, inCache ) -> {
            if ( inCache != null && !inCache.getNodeId().equals( nodeBranchEntry.getNodeId() ) )
            {
                alreadyExists[0] = true;
                return inCache;
            }

            this.storageDao.store( BranchStorageRequestFactory.create( nodeBranchEntry, cK.getRepositoryId(), cK.getBranch() ) );
            return nodeBranchEntry;
        } );
        if ( alreadyExists[0] )
        {
            throw new NodeAlreadyExistAtPathException( nodeBranchEntry.getNodePath(), repositoryId, branch );
        }
    }

    @Override
    public void delete( final Collection<NodeBranchEntry> entries, final InternalContext context )
    {
        final RepositoryId repositoryId = context.getRepositoryId();
        final Branch branch = context.getBranch();

        try
        {
            storageDao.delete( DeleteRequests.create()
                                   .ids( entries.stream()
                                             .map( entry -> BranchDocumentId.asRoutableId( entry.getNodeId(), branch ) )
                                             .collect( Collectors.toList() ) )
                                   .settings( createStorageSettings( repositoryId ) )
                                   .build() );
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

    private NodeBranchEntry doGetById( final NodeId nodeId, final InternalContext context )
    {
        final GetByIdRequest getByIdRequest = GetByIdRequest.create()
            .id( BranchDocumentId.asString( nodeId, context.getBranch() ) )
            .storageSettings( createStorageSettings( context.getRepositoryId() ) )
            .searchPreference( context.getSearchPreference() )
            .returnFields( BRANCH_RETURN_FIELDS )
            .routing( nodeId.toString() )
            .build();
        final GetResult getResult = this.storageDao.getById( getByIdRequest );

        if ( getResult.isEmpty() )
        {
            return null;
        }

        return NodeBranchVersionFactory.create( getResult.getReturnValues() );
    }

    @Override
    public NodeBranchEntries get( final Iterable<NodeId> nodeIds, final InternalContext context )
    {
        final GetByIdsRequest getByIdsRequest = new GetByIdsRequest( context.getSearchPreference() );

        final StorageSource storageSettings = createStorageSettings( context.getRepositoryId() );

        for ( final NodeId nodeId : nodeIds )
        {
            getByIdsRequest.add( GetByIdRequest.create()
                                     .id( BranchDocumentId.asString( nodeId, context.getBranch() ) )
                                     .storageSettings( storageSettings )
                                     .searchPreference( context.getSearchPreference() )
                                     .returnFields( BRANCH_RETURN_FIELDS )
                                     .routing( nodeId.toString() )
                                     .build() );
        }

        final List<GetResult> getResults = this.storageDao.getByIds( getByIdsRequest );

        final NodeBranchEntries.Builder builder = NodeBranchEntries.create();

        getResults.stream()
            .filter( Predicate.not( GetResult::isEmpty ) )
            .map( GetResult::getReturnValues )
            .map( NodeBranchVersionFactory::create )
            .forEach( builder::add );

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

            storageDao.refresh( StoreStorageName.from( repositoryId ) );

            final NodeBranchQuery query = NodeBranchQuery.create()
                .addQueryFilter( ValueFilter.create()
                                     .fieldName( BranchIndexPath.PATH.getPath() )
                                     .addValue( ValueFactory.newString( nodePath.toString() ) )
                                     .build() )
                .addQueryFilter( ValueFilter.create()
                                     .fieldName( BranchIndexPath.BRANCH_NAME.getPath() )
                                     .addValue( ValueFactory.newString( branch.getValue() ) )
                                     .build() )
                .size( 1 )
                .build();

            final SearchResult result = this.searchDao.search( SearchRequest.create()
                                                                   .searchSource( SingleRepoStorageSource.create( repositoryId,
                                                                                                                  StaticStorageType.BRANCH ) )
                                                                   .returnFields( BRANCH_RETURN_FIELDS )
                                                                   .query( query )
                                                                   .searchPreference( context.getSearchPreference() )
                                                                   .build() );
            if ( result.isEmpty() )
            {
                return null;
            }
            return NodeBranchVersionFactory.create( result.getHits().getFirst().getReturnValues() );
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

    private static StorageSource createStorageSettings( final RepositoryId repositoryId )
    {
        return StorageSource.create().storageName( StoreStorageName.from( repositoryId ) ).storageType( StaticStorageType.BRANCH ).build();
    }
}

