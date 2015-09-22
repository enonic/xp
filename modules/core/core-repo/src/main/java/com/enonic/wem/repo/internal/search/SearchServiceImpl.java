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
import com.enonic.wem.repo.internal.storage.StorageSettings;
import com.enonic.wem.repo.internal.storage.StoreStorageName;
import com.enonic.wem.repo.internal.storage.branch.BranchIndexPath;
import com.enonic.wem.repo.internal.storage.branch.NodeBranchQuery;
import com.enonic.wem.repo.internal.storage.branch.NodeBranchQueryResult;
import com.enonic.wem.repo.internal.storage.branch.NodeBranchQueryResultFactory;
import com.enonic.wem.repo.internal.storage.result.SearchResult;
import com.enonic.wem.repo.internal.version.NodeVersionQuery;
import com.enonic.wem.repo.internal.version.NodeVersionQueryResultFactory;
import com.enonic.wem.repo.internal.version.VersionIndexPath;
import com.enonic.wem.repo.internal.version.VersionService;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeVersionDiffQuery;
import com.enonic.xp.node.NodeVersionDiffResult;
import com.enonic.xp.node.NodeVersionQueryResult;

@Component
public class SearchServiceImpl
    implements SearchService
{
    private VersionService versionService;

    private SearchDao searchDao;

    private static final ReturnFields VERSION_RETURN_FIELDS =
        ReturnFields.from( VersionIndexPath.VERSION_ID, VersionIndexPath.TIMESTAMP, VersionIndexPath.NODE_PATH, VersionIndexPath.NODE_ID );

    private static final ReturnFields BRANCH_RETURN_FIELDS =
        ReturnFields.from( BranchIndexPath.NODE_ID, BranchIndexPath.VERSION_ID, BranchIndexPath.STATE, BranchIndexPath.PATH,
                           BranchIndexPath.TIMESTAMP );

    @Override
    public NodeQueryResult search( final NodeQuery query, final InternalContext context )
    {
        final SearchRequest searchRequest = SearchRequest.create().
            settings( StorageSettings.create().
                storageName( SearchStorageName.from( context.getRepositoryId() ) ).
                storageType( SearchStorageType.from( context.getBranch() ) ).
                build() ).
            acl( context.getPrincipalsKeys() ).
            query( query ).
            build();

        final SearchResult result = searchDao.search( searchRequest );

        return NodeQueryResultFactory.create( result );
    }

    @Override
    public NodeBranchQueryResult search( final NodeBranchQuery nodeBranchQuery, final InternalContext context )
    {
        final SearchRequest searchRequest = SearchRequest.create().
            settings( StorageSettings.create().
                storageName( StoreStorageName.from( context.getRepositoryId() ) ).
                storageType( StaticStorageType.BRANCH ).
                build() ).
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
        final SearchRequest searchRequest = SearchRequest.create().
            settings( StorageSettings.create().
                storageName( StoreStorageName.from( context.getRepositoryId() ) ).
                storageType( StaticStorageType.VERSION ).
                build() ).
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
    public NodeVersionDiffResult diffNodeVersions( final NodeVersionDiffQuery query, final InternalContext context )
    {
        return this.versionService.diff( query, context );
    }

    @Reference
    public void setVersionService( final VersionService versionService )
    {
        this.versionService = versionService;
    }

    @Reference
    public void setSearchDao( final SearchDao searchDao )
    {
        this.searchDao = searchDao;
    }
}
