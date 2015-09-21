package com.enonic.wem.repo.internal.search;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.wem.repo.internal.InternalContext;
import com.enonic.wem.repo.internal.index.query.NodeQueryResult;
import com.enonic.wem.repo.internal.storage.branch.NodeBranchQuery;
import com.enonic.wem.repo.internal.storage.branch.NodeBranchQueryResult;
import com.enonic.wem.repo.internal.version.FindVersionsQuery;
import com.enonic.wem.repo.internal.version.VersionService;
import com.enonic.xp.node.FindNodeVersionsResult;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeVersionDiffQuery;
import com.enonic.xp.node.NodeVersionDiffResult;
import com.enonic.xp.node.NodeVersionIds;
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
        return searchDao.find( query, context );
    }

    @Override
    public NodeVersionIds search( final NodeIds nodeIds, final OrderExpressions orderExprs, final InternalContext context )
    {
        return searchDao.find( nodeIds, orderExprs, context );
    }

    @Override
    public NodeBranchQueryResult search( final NodeBranchQuery nodeBranchQuery, final InternalContext context )
    {
        return this.searchDao.find( nodeBranchQuery, context );
    }

    @Override
    public FindNodeVersionsResult findVersions( final FindVersionsQuery query, final InternalContext context )
    {
        return this.versionService.findVersions( query, context );
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
