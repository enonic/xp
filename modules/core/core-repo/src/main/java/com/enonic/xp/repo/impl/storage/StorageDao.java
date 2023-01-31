package com.enonic.xp.repo.impl.storage;

import java.util.List;

import com.enonic.xp.repo.impl.StorageName;
import com.enonic.xp.repo.impl.elasticsearch.document.IndexDocument;

public interface StorageDao
{
    String store( StoreRequest request );

    void store( IndexDocument indexDocument );

    void delete( DeleteRequests request );

    GetResult getById( GetByIdRequest request );

    List<GetResult> getByIds( GetByIdsRequest requests );

    void copy( CopyRequest request );

    void refresh( StorageName storageName );
}
