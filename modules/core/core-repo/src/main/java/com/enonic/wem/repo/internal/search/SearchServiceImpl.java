package com.enonic.wem.repo.internal.search;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.wem.repo.internal.InternalContext;
import com.enonic.wem.repo.internal.branch.BranchService;
import com.enonic.wem.repo.internal.index.IndexContext;
import com.enonic.wem.repo.internal.index.query.NodeQueryResult;
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

    private BranchService branchService;

    @Override
    public NodeQueryResult find( final NodeQuery query, final IndexContext context )
    {
        return null;
    }

    @Override
    public NodeVersionIds find( final NodeIds nodeIds, final OrderExpressions orderExprs, final IndexContext indexContext )
    {
        return null;
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
    public void setBranchService( final BranchService branchService )
    {
        this.branchService = branchService;
    }
}
