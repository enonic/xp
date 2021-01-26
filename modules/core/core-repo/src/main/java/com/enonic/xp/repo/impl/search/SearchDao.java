package com.enonic.xp.repo.impl.search;

import com.enonic.xp.repo.impl.search.result.SearchResult;

public interface SearchDao
{
    SearchResult search( SearchRequest searchRequest );
}
