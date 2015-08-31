package com.enonic.wem.repo.internal.storage;

import com.enonic.wem.repo.internal.index.result.GetResult;
import com.enonic.wem.repo.internal.index.result.GetResultNew;

public interface StorageCache
{
    String put( final StoreRequest request );

    void remove( final DeleteRequest request );

    GetResultNew getById( final GetByIdRequest request );

    GetResult getByPath( final GetByPathRequest request );

    GetResult getByParent( final GetByParentRequest request );
}
