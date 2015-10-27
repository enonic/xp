package com.enonic.xp.repo.impl.storage;

import com.enonic.xp.repo.impl.search.result.SearchResult;

public interface StorageDao
{
    String store( final StoreRequest request );

    boolean delete( final DeleteRequest request );

    GetResult getById( final GetByIdRequest request );

    GetResults getByIds( final GetByIdsRequest requests );

    SearchResult getByValues( final GetByValuesRequest request );
}
