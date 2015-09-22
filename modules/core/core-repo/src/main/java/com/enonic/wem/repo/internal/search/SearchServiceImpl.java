package com.enonic.wem.repo.internal.search;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.wem.repo.internal.InternalContext;
import com.enonic.wem.repo.internal.index.query.NodeQueryResult;
import com.enonic.wem.repo.internal.index.query.NodeQueryResultFactory;
import com.enonic.wem.repo.internal.storage.SearchStorageName;
import com.enonic.wem.repo.internal.storage.SearchStorageType;
import com.enonic.wem.repo.internal.storage.StorageSettings;
import com.enonic.wem.repo.internal.storage.branch.NodeBranchQuery;
import com.enonic.wem.repo.internal.storage.branch.NodeBranchQueryResult;
import com.enonic.wem.repo.internal.storage.result.SearchResult;
import com.enonic.wem.repo.internal.version.NodeVersionQuery;
import com.enonic.wem.repo.internal.version.VersionService;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeVersionDiffQuery;
import com.enonic.xp.node.NodeVersionDiffResult;
import com.enonic.xp.node.NodeVersionIds;
import com.enonic.xp.node.NodeVersionQueryResult;
import com.enonic.xp.query.expr.OrderExpressions;

@Component
public class SearchServiceImpl
    implements SearchService
{
    private VersionService versionService;

    private SearchDao searchDao;

    @Override
    public NodeQueryResult search( final NodeQuery query, final InternalContext context )
    {
        final SearchRequest searchRequest = SearchRequest.create().
            settings( StorageSettings.create().
                storageName( SearchStorageName.from( context.getRepositoryId() ) ).
                storageType( SearchStorageType.from( context.getBranch() ) ).
                acl( context.getPrincipalsKeys() ).
                build() ).
            query( query ).
            build();

        final SearchResult result = searchDao.search( searchRequest );

        return NodeQueryResultFactory.create( result );

    }

    @Override
    public NodeBranchQueryResult search( final NodeBranchQuery nodeBranchQuery, final InternalContext context )
    {
        return this.searchDao.find( nodeBranchQuery, context );
    }

    @Override
    public NodeVersionQueryResult search( final NodeVersionQuery query, final InternalContext context )
    {
        return this.versionService.findVersions( query, context );
    }

    @Override
    public NodeVersionIds toBeRewrittenToNodeVersionQuery( final NodeIds nodeIds, final OrderExpressions orderExprs,
                                                           final InternalContext context )
    {
        return searchDao.find( nodeIds, orderExprs, context );
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
