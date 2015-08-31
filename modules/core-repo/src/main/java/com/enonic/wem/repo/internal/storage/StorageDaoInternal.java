package com.enonic.wem.repo.internal.storage;

import com.enonic.wem.repo.internal.index.result.GetResultNew;

public interface StorageDaoInternal
{
    String store( final StoreRequest request );

    boolean delete( final DeleteRequest request );

    GetResultNew getById( final GetByIdRequest request );

    GetResultNew getByPath( final GetByPathRequest request );

    GetResultNew getByParent( final GetByParentRequest request );

}
