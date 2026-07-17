package com.enonic.xp.repo.impl.elasticsearch.storage;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.storage.spi.ReturnFields;
import com.enonic.xp.storage.spi.SingleRepoStorageSource;
import com.enonic.xp.repo.impl.StorageSource;
import com.enonic.xp.repo.impl.branch.search.NodeBranchQuery;
import com.enonic.xp.repo.impl.branch.storage.BranchDocumentId;
import com.enonic.xp.repo.impl.branch.storage.BranchIndexPath;
import com.enonic.xp.repo.impl.branch.storage.BranchStorageRequestFactory;
import com.enonic.xp.repo.impl.branch.storage.NodeBranchVersionFactory;
import com.enonic.xp.repo.impl.commit.storage.CommitIndexPath;
import com.enonic.xp.repo.impl.commit.storage.CommitStorageRequestFactory;
import com.enonic.xp.repo.impl.commit.storage.NodeCommitEntryFactory;
import com.enonic.xp.repo.impl.search.SearchDao;
import com.enonic.xp.storage.spi.SearchRequest;
import com.enonic.xp.storage.spi.SearchResult;
import com.enonic.xp.repo.impl.storage.DeleteRequests;
import com.enonic.xp.repo.impl.storage.GetByIdRequest;
import com.enonic.xp.repo.impl.storage.GetByIdsRequest;
import com.enonic.xp.repo.impl.storage.GetResult;
import com.enonic.xp.repo.impl.storage.RoutableId;
import com.enonic.xp.repo.impl.storage.SearchPreferences;
import com.enonic.xp.repo.impl.storage.StaticStorageType;
import com.enonic.xp.repo.impl.storage.StorageDao;
import com.enonic.xp.repo.impl.storage.StoreStorageName;
import com.enonic.xp.repo.impl.version.NodeVersionFactory;
import com.enonic.xp.repo.impl.version.VersionIndexPath;
import com.enonic.xp.repo.impl.version.VersionStorageDocFactory;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.storage.spi.BranchEntryRecord;
import com.enonic.xp.storage.spi.CommitRecord;
import com.enonic.xp.storage.spi.NodeStore;
import com.enonic.xp.storage.spi.SearchPreference;
import com.enonic.xp.storage.spi.StaticStoreType;
import com.enonic.xp.storage.spi.VersionRecord;

/**
 * Elasticsearch-backed {@link NodeStore}: adapts the typed SPI records onto the existing
 * {@code *StorageRequestFactory} + {@link StorageDao} machinery, unchanged, so serialized
 * storage documents stay byte-identical to before the storage-SPI extraction (Phase 0,
 * Gate B — see {@code nodb/BUILD-PHASE-0.md}). Confines {@code StorageDao} and the storage
 * request factories to this package: the ES-free service layer
 * ({@code BranchServiceImpl}/{@code VersionServiceImpl}/{@code CommitServiceImpl}) only
 * ever sees {@link NodeStore}.
 * <p>
 * Registered with the {@code storage.backend=elasticsearch} service property (Phase 0,
 * Gate D). Only one backend exists yet, so consumers still plain-{@code @Reference} this
 * service; Phase 1 backend selection becomes a {@code @Reference(target = "(storage.backend=...)")}
 * filter on this same property, not a rewrite.
 */
@Component(service = NodeStore.class, property = "storage.backend=elasticsearch")
public class ElasticsearchNodeStore
    implements NodeStore
{
    private static final ReturnFields BRANCH_RETURN_FIELDS = ReturnFields.from( BranchIndexPath.entryFields() );

    private static final ReturnFields VERSION_RETURN_FIELDS = ReturnFields.from( VersionIndexPath.entryFields() );

    private static final ReturnFields COMMIT_RETURN_FIELDS = ReturnFields.from( CommitIndexPath.entryFields() );

    private final StorageDao storageDao;

    private final SearchDao searchDao;

    @Activate
    public ElasticsearchNodeStore( @Reference final StorageDao storageDao, @Reference final SearchDao searchDao )
    {
        this.storageDao = storageDao;
        this.searchDao = searchDao;
    }

    // --- branch entries ---

    @Override
    public void storeBranchEntry( final RepositoryId repositoryId, final Branch branch, final BranchEntryRecord entry )
    {
        storageDao.store( BranchStorageRequestFactory.create( NodeBranchVersionFactory.fromRecord( entry ), repositoryId, branch ) );
    }

    @Override
    public void deleteBranchEntries( final RepositoryId repositoryId, final Branch branch, final Collection<String> nodeIds )
    {
        storageDao.delete( DeleteRequests.create()
                                .ids( nodeIds.stream()
                                          .map( nodeId -> BranchDocumentId.asRoutableId( NodeId.from( nodeId ), branch ) )
                                          .collect( Collectors.toList() ) )
                                .settings( branchStorageSettings( repositoryId ) )
                                .build() );
    }

    @Override
    public boolean existsBranchEntry( final RepositoryId repositoryId, final Branch branch, final String nodeId,
                                       final SearchPreference searchPreference )
    {
        final GetByIdRequest getByIdRequest = GetByIdRequest.create()
            .id( BranchDocumentId.asString( NodeId.from( nodeId ), branch ) )
            .storageSettings( branchStorageSettings( repositoryId ) )
            .searchPreference( SearchPreferences.fromSpi( searchPreference ) )
            .routing( nodeId )
            .build();
        return !storageDao.getById( getByIdRequest ).isEmpty();
    }

    @Override
    public BranchEntryRecord getBranchEntry( final RepositoryId repositoryId, final Branch branch, final String nodeId,
                                              final SearchPreference searchPreference )
    {
        final GetByIdRequest getByIdRequest = GetByIdRequest.create()
            .id( BranchDocumentId.asString( NodeId.from( nodeId ), branch ) )
            .storageSettings( branchStorageSettings( repositoryId ) )
            .searchPreference( SearchPreferences.fromSpi( searchPreference ) )
            .returnFields( BRANCH_RETURN_FIELDS )
            .routing( nodeId )
            .build();

        final GetResult getResult = storageDao.getById( getByIdRequest );

        return getResult.isEmpty()
            ? null
            : NodeBranchVersionFactory.toRecord( NodeBranchVersionFactory.create( getResult.getReturnValues() ) );
    }

    @Override
    public List<BranchEntryRecord> getBranchEntries( final RepositoryId repositoryId, final Branch branch,
                                                       final Collection<String> nodeIds, final SearchPreference searchPreference )
    {
        final GetByIdsRequest getByIdsRequest = new GetByIdsRequest( SearchPreferences.fromSpi( searchPreference ) );

        final StorageSource storageSettings = branchStorageSettings( repositoryId );

        for ( final String nodeId : nodeIds )
        {
            getByIdsRequest.add( GetByIdRequest.create()
                                     .id( BranchDocumentId.asString( NodeId.from( nodeId ), branch ) )
                                     .storageSettings( storageSettings )
                                     .searchPreference( SearchPreferences.fromSpi( searchPreference ) )
                                     .returnFields( BRANCH_RETURN_FIELDS )
                                     .routing( nodeId )
                                     .build() );
        }

        return storageDao.getByIds( getByIdsRequest )
            .stream()
            .filter( Predicate.not( GetResult::isEmpty ) )
            .map( GetResult::getReturnValues )
            .map( NodeBranchVersionFactory::create )
            .map( NodeBranchVersionFactory::toRecord )
            .collect( Collectors.toList() );
    }

    @Override
    public BranchEntryRecord getBranchEntryByPath( final RepositoryId repositoryId, final Branch branch, final String nodePath,
                                                    final SearchPreference searchPreference )
    {
        storageDao.refresh( StoreStorageName.from( repositoryId ) );

        final NodeBranchQuery query = NodeBranchQuery.create()
            .addQueryFilter(
                ValueFilter.create().fieldName( BranchIndexPath.PATH.getPath() ).addValue( ValueFactory.newString( nodePath ) ).build() )
            .addQueryFilter( ValueFilter.create()
                                 .fieldName( BranchIndexPath.BRANCH_NAME.getPath() )
                                 .addValue( ValueFactory.newString( branch.getValue() ) )
                                 .build() )
            .size( 1 )
            .build();

        final SearchResult result = searchDao.search( SearchRequest.create()
                                                           .searchSource(
                                                               SingleRepoStorageSource.create( repositoryId, StaticStoreType.BRANCH ) )
                                                           .returnFields( BRANCH_RETURN_FIELDS )
                                                           .query( query )
                                                           .searchPreference( searchPreference )
                                                           .build() );

        return result.isEmpty()
            ? null
            : NodeBranchVersionFactory.toRecord( NodeBranchVersionFactory.create( result.getHits().getFirst().getReturnValues() ) );
    }

    @Override
    public List<Branch> getBranchesWithNode( final RepositoryId repositoryId, final String nodeId )
    {
        final NodeBranchQuery query = NodeBranchQuery.create()
            .addQueryFilter(
                ValueFilter.create().fieldName( BranchIndexPath.NODE_ID.getPath() ).addValue( ValueFactory.newString( nodeId ) ).build() )
            .build();

        final SearchResult searchResult = searchDao.search( SearchRequest.create()
                                                                 .searchSource( SingleRepoStorageSource.create( repositoryId,
                                                                                                                 StaticStoreType.BRANCH ) )
                                                                 .query( query )
                                                                 .build() );

        return searchResult.getHits()
            .stream()
            .map( hit -> Branch.from( hit.getId().substring( hit.getId().lastIndexOf( '_' ) + 1 ) ) )
            .collect( Collectors.toList() );
    }

    private static StorageSource branchStorageSettings( final RepositoryId repositoryId )
    {
        return StorageSource.create().storageName( StoreStorageName.from( repositoryId ) ).storageType( StaticStorageType.BRANCH ).build();
    }

    // --- versions ---

    @Override
    public void storeVersion( final RepositoryId repositoryId, final VersionRecord version )
    {
        storageDao.store( VersionStorageDocFactory.create( NodeVersionFactory.fromRecord( version ), repositoryId ) );
    }

    @Override
    public void deleteVersion( final RepositoryId repositoryId, final String versionId )
    {
        storageDao.delete(
            DeleteRequests.create().ids( List.of( new RoutableId( versionId ) ) ).settings( versionStorageSettings( repositoryId ) ).build() );
    }

    @Override
    public VersionRecord getVersion( final RepositoryId repositoryId, final String versionId, final SearchPreference searchPreference )
    {
        final GetByIdRequest getByIdRequest = GetByIdRequest.create()
            .id( versionId )
            .returnFields( VERSION_RETURN_FIELDS )
            .storageSettings( versionStorageSettings( repositoryId ) )
            .searchPreference( SearchPreferences.fromSpi( searchPreference ) )
            .build();

        final GetResult getResult = storageDao.getById( getByIdRequest );

        return getResult.isEmpty() ? null : NodeVersionFactory.toRecord( NodeVersionFactory.create( getResult.getReturnValues() ) );
    }

    private static StorageSource versionStorageSettings( final RepositoryId repositoryId )
    {
        return StorageSource.create()
            .storageName( StoreStorageName.from( repositoryId ) )
            .storageType( StaticStorageType.VERSION )
            .build();
    }

    // --- commits ---

    @Override
    public void storeCommit( final RepositoryId repositoryId, final CommitRecord commit )
    {
        storageDao.store( CommitStorageRequestFactory.create( NodeCommitEntryFactory.fromRecord( commit ), repositoryId ) );
    }

    @Override
    public CommitRecord getCommit( final RepositoryId repositoryId, final String commitId, final SearchPreference searchPreference )
    {
        final GetByIdRequest getByIdRequest = GetByIdRequest.create()
            .id( commitId )
            .storageSettings( commitStorageSettings( repositoryId ) )
            .searchPreference( SearchPreferences.fromSpi( searchPreference ) )
            .returnFields( COMMIT_RETURN_FIELDS )
            .routing( commitId )
            .build();

        final GetResult getResult = storageDao.getById( getByIdRequest );

        return getResult.isEmpty() ? null : NodeCommitEntryFactory.toRecord( NodeCommitEntryFactory.create( getResult.getReturnValues() ) );
    }

    private static StorageSource commitStorageSettings( final RepositoryId repositoryId )
    {
        return StorageSource.create().storageName( StoreStorageName.from( repositoryId ) ).storageType( StaticStorageType.COMMIT ).build();
    }
}
