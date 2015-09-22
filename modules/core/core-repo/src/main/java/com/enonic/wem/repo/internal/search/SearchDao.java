package com.enonic.wem.repo.internal.search;

import com.enonic.wem.repo.internal.InternalContext;
import com.enonic.wem.repo.internal.storage.result.SearchResult;
import com.enonic.wem.repo.internal.version.NodeVersionQuery;
import com.enonic.xp.node.NodeVersionDiffQuery;
import com.enonic.xp.node.NodeVersionDiffResult;

public interface SearchDao
{
    SearchResult find( final NodeVersionQuery query, final InternalContext context );

    SearchResult search( final SearchRequest searchRequest );

    NodeVersionDiffResult versionDiff( final NodeVersionDiffQuery query, final InternalContext context );

}
