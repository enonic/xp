package com.enonic.wem.repo.internal.storage;


import com.enonic.wem.repo.internal.InternalContext;
import com.enonic.wem.repo.internal.storage.result.GetResult;

public interface StorageService
{
    String store( final StoreRequest request, final InternalContext context );

    boolean delete( final DeleteRequest request, final InternalContext context );

    GetResult getById( final GetByIdRequest request, final InternalContext context );

    GetResult getByPath( final GetByPathRequest request, final InternalContext context );

    GetResult getByParent( final GetByParentRequest request, final InternalContext context );

}
