package com.enonic.xp.repo.impl.storage;

import java.util.Collection;

import com.enonic.xp.repo.impl.elasticsearch.document.IndexDocument;

public interface StorageDao
{
    String store( final StoreRequest request );

    void store( final Collection<IndexDocument> indexDocuments );

    void delete( final DeleteRequest request );

    void delete( final DeleteRequests request );

    GetResult getById( final GetByIdRequest request );

    GetResults getByIds( final GetByIdsRequest requests );

    void copy( final CopyRequest request );

}
