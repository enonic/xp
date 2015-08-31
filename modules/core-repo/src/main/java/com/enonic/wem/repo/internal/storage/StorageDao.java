package com.enonic.wem.repo.internal.storage;


import com.enonic.wem.repo.internal.index.result.GetResult;
import com.enonic.wem.repo.internal.index.result.GetResultNew;

public interface StorageDao
{
    String store( final StoreRequest request );

    boolean delete( final DeleteRequest request );

    GetResultNew getById( final GetByIdRequest request );

    GetResult getByPath( final GetByPathRequest request );

    GetResult getByParent( final GetByParentRequest request );

}
