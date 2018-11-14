package com.enonic.xp.repo.impl.storage;

import java.util.Collection;

import com.enonic.xp.node.DeleteNodeListener;
import com.enonic.xp.repo.impl.elasticsearch.document.IndexDocument;

public interface StorageDao
{
    String store( final StoreRequest request );

    void store( final Collection<IndexDocument> indexDocuments );

    boolean delete( final DeleteRequest request );

    void delete( final DeleteRequests request );

    void delete( final DeleteRequests requests, final DeleteNodeListener listener );

    GetResult getById( final GetByIdRequest request );

    GetResults getByIds( final GetByIdsRequest requests );

    void copy( final CopyRequest request );

}
