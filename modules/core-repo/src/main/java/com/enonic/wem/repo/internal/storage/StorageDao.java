package com.enonic.wem.repo.internal.storage;

import com.enonic.wem.repo.internal.storage.result.GetResult;
import com.enonic.wem.repo.internal.storage.result.SearchResult;

public interface StorageDao
{
    String store( final StoreRequest request );

    boolean delete( final DeleteRequest request );

    GetResult getById( final GetByIdRequest request );

    SearchResult getByValues( final GetByValuesRequest request );

}
