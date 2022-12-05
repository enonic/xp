package com.enonic.xp.repo.impl.storage;

import java.util.Collection;
import java.util.List;

import com.enonic.xp.repo.impl.elasticsearch.document.IndexDocument;

public interface StorageDao
{
    String store( StoreRequest request );

    void store( Collection<IndexDocument> indexDocuments );

    boolean delete( DeleteRequest request );

    void delete( DeleteRequests request );

    GetResult getById( GetByIdRequest request );

    List<GetResult> getByIds( GetByIdsRequest requests );

    void copy( CopyRequest request );
}
