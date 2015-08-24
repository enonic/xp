package com.enonic.wem.repo.internal.storage;


import com.enonic.wem.repo.internal.index.result.GetResult;

public interface StorageDao
{
    String store( final StoreRequest doc );

    GetResult getById( final GetByIdRequest query );

    GetResult getByPath( final GetByPathRequest query );

    GetResult getByParent( final GetByParentRequest query );

}
