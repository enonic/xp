package com.enonic.wem.repo.internal.search;

import com.enonic.wem.repo.internal.storage.result.SearchResult;

public interface SearchDao
{
    SearchResult search( final SearchRequest searchRequest );

}
