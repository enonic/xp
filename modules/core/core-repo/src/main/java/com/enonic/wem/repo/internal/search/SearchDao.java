package com.enonic.wem.repo.internal.search;

import com.enonic.wem.repo.internal.InternalContext;
import com.enonic.wem.repo.internal.storage.result.SearchResult;
import com.enonic.xp.node.NodeVersionDiffQuery;
import com.enonic.xp.node.NodeVersionDiffResult;

public interface SearchDao
{
    SearchResult search( final SearchRequest searchRequest );

    NodeVersionDiffResult versionDiff( final NodeVersionDiffQuery query, final InternalContext context );
}
