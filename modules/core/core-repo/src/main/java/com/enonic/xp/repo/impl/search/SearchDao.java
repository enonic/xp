package com.enonic.xp.repo.impl.search;

import com.enonic.xp.storage.spi.SearchRequest;
import com.enonic.xp.storage.spi.SearchResult;

public interface SearchDao
{
    SearchResult search( SearchRequest searchRequest );
}
