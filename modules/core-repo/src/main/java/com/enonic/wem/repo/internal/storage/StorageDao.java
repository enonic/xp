package com.enonic.wem.repo.internal.storage;

import com.enonic.wem.repo.internal.storage.result.GetResult;

public interface StorageDao
{
    String store( final StoreRequest request );

    boolean delete( final DeleteRequest request );

    GetResult getById( final GetByIdRequest request );

    GetResult getByPath( final GetByPathRequest request );

    GetResult getByParent( final GetByParentRequest request );

}
