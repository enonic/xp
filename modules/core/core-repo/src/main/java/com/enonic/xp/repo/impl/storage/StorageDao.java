package com.enonic.xp.repo.impl.storage;

import java.util.Collection;

import com.enonic.xp.repo.impl.elasticsearch.document.IndexDocument;

public interface StorageDao
{
    String store( StoreRequest request );

    void store( Collection<IndexDocument> indexDocuments );

    boolean delete( DeleteRequest request );

    void delete( DeleteRequests request );

    GetResult getById( GetByIdRequest request );

    GetResults getByIds( GetByIdsRequest requests );

    void copy( CopyRequest request );
}
