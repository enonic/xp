package com.enonic.wem.repo.internal.search;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.wem.repo.internal.InternalContext;
import com.enonic.wem.repo.internal.index.query.NodeQueryResult;
import com.enonic.wem.repo.internal.index.query.NodeQueryResultFactory;
import com.enonic.wem.repo.internal.storage.ReturnFields;
import com.enonic.wem.repo.internal.storage.SearchStorageName;
import com.enonic.wem.repo.internal.storage.SearchStorageType;
import com.enonic.wem.repo.internal.storage.StaticStorageType;
import com.enonic.wem.repo.internal.storage.StorageName;
import com.enonic.wem.repo.internal.storage.StorageSettings;
import com.enonic.wem.repo.internal.storage.StorageType;
import com.enonic.wem.repo.internal.storage.StoreStorageName;
import com.enonic.wem.repo.internal.storage.branch.BranchIndexPath;
import com.enonic.wem.repo.internal.storage.branch.NodeBranchQuery;
import com.enonic.wem.repo.internal.storage.branch.NodeBranchQueryResult;
import com.enonic.wem.repo.internal.storage.branch.NodeBranchQueryResultFactory;
import com.enonic.wem.repo.internal.storage.result.SearchHit;
import com.enonic.wem.repo.internal.storage.result.SearchResult;
import com.enonic.wem.repo.internal.version.NodeVersionDiffQuery;
import com.enonic.wem.repo.internal.version.NodeVersionQuery;
import com.enonic.wem.repo.internal.version.NodeVersionQueryResultFactory;
import com.enonic.wem.repo.internal.version.VersionIndexPath;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeVersionDiffResult;
import com.enonic.xp.node.NodeVersionQueryResult;

@Component
public class SearchServiceImpl
    implements SearchService
{
    private SearchDao searchDao;

    private static final ReturnFields VERSION_RETURN_FIELDS =
        ReturnFields.from( VersionIndexPath.VERSION_ID, VersionIndexPath.TIMESTAMP, VersionIndexPath.NODE_PATH, VersionIndexPath.NODE_ID );

    private static final ReturnFields BRANCH_RETURN_FIELDS =
        ReturnFields.from( BranchIndexPath.NODE_ID, BranchIndexPath.VERSION_ID, BranchIndexPath.STATE, BranchIndexPath.PATH,
                           BranchIndexPath.TIMESTAMP );

    @Override
    public NodeQueryResult search( final NodeQuery query, final InternalContext context )
    {
        final StorageType storageType = SearchStorageType.from( context.getBranch() );

        final StorageName storageName = SearchStorageName.from( context.getRepositoryId() );

        final SearchRequest searchRequest = SearchRequest.create().
            settings( createSettings( storageType, storageName ) ).
            acl( context.getPrincipalsKeys() ).
            query( query ).
            build();

        final SearchResult result = searchDao.search( searchRequest );

        return NodeQueryResultFactory.create( result );
    }

    @Override
    public NodeBranchQueryResult search( final NodeBranchQuery nodeBranchQuery, final InternalContext context )
    {
        final StorageType storageType = StaticStorageType.BRANCH;
        final StorageName storageName = StoreStorageName.from( context.getRepositoryId() );

        final SearchRequest searchRequest = SearchRequest.create().
            settings( createSettings( storageType, storageName ) ).
            returnFields( BRANCH_RETURN_FIELDS ).
            acl( context.getPrincipalsKeys() ).
            query( nodeBranchQuery ).
            build();

        final SearchResult result = searchDao.search( searchRequest );

        if ( result.isEmpty() )
        {
            return NodeBranchQueryResult.empty();
        }

        return NodeBranchQueryResultFactory.create( result );
    }

    @Override
    public NodeVersionQueryResult search( final NodeVersionQuery query, final InternalContext context )
    {
        final StorageType storageType = StaticStorageType.VERSION;
        final StorageName storageName = StoreStorageName.from( context.getRepositoryId() );

        final SearchRequest searchRequest = SearchRequest.create().
            settings( createSettings( storageType, storageName ) ).
            returnFields( VERSION_RETURN_FIELDS ).
            acl( context.getPrincipalsKeys() ).
            query( query ).
            build();

        final SearchResult result = searchDao.search( searchRequest );

        if ( result.isEmpty() )
        {
            return NodeVersionQueryResult.create().build();
        }

        return NodeVersionQueryResultFactory.create( query, result );
    }

    @Override
    public NodeVersionDiffResult search( final NodeVersionDiffQuery query, final InternalContext context )
    {
        final StorageType storageType = StaticStorageType.VERSION;
        final StorageName storageName = StoreStorageName.from( context.getRepositoryId() );

        final SearchRequest searchRequest = SearchRequest.create().
            settings( createSettings( storageType, storageName ) ).
            returnFields( VERSION_RETURN_FIELDS ).
            acl( context.getPrincipalsKeys() ).
            query( query ).
            build();

        final SearchResult result = searchDao.search( searchRequest );

        if ( result.isEmpty() )
        {
            return NodeVersionDiffResult.empty();
        }

        final NodeVersionDiffResult.Builder builder = NodeVersionDiffResult.create();

        for ( final SearchHit hit : result.getResults() )
        {
            builder.add( NodeId.from( hit.getField( VersionIndexPath.NODE_ID.toString() ).getSingleValue().toString() ) );
        }

        return builder.build();
    }

    private StorageSettings createSettings( final StorageType storageType, final StorageName storageName )
    {
        return StorageSettings.create().
            storageName( storageName ).
            storageType( storageType ).
            build();
    }

    @Reference
    public void setSearchDao( final SearchDao searchDao )
    {
        this.searchDao = searchDao;
    }
}
