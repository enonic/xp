package com.enonic.wem.repo.internal.storage;

import com.enonic.wem.repo.internal.index.result.GetResult;

public interface StorageDaoInternal
{
    String store( final StoreRequest request );

    boolean delete( final DeleteRequest request );

    GetResult getById( final GetByIdRequest request );

    GetResult getByPath( final GetByPathRequest request );

    GetResult getByParent( final GetByParentRequest request );

}
