package com.enonic.wem.repo.internal.storage;

import com.enonic.wem.repo.internal.storage.result.GetResult;

public interface StorageCache
{
    String put( final StoreRequest request );

    void remove( final DeleteRequest request );

    GetResult getById( final GetByIdRequest request );

    GetResult getByPath( final GetByPathRequest request );

    GetResult getByParent( final GetByParentRequest request );
}
