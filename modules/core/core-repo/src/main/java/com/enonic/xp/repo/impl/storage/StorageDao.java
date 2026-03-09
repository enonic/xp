package com.enonic.xp.repo.impl.storage;

import java.util.List;

import com.enonic.xp.repo.impl.StorageName;

public interface StorageDao
{
    String store( StoreRequest request );

    void store( IndexStoreRequest request );

    void delete( DeleteRequests request );

    GetResult getById( GetByIdRequest request );

    List<GetResult> getByIds( GetByIdsRequest requests );

    void refresh( StorageName storageName );
}
