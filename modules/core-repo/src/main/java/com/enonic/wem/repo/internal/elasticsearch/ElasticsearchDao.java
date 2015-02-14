package com.enonic.wem.repo.internal.elasticsearch;

import java.util.Collection;

import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.snapshots.SnapshotInfo;

import com.enonic.xp.core.snapshot.RestoreParams;
import com.enonic.xp.core.snapshot.RestoreResult;
import com.enonic.xp.core.snapshot.SnapshotParams;
import com.enonic.xp.core.snapshot.SnapshotResult;
import com.enonic.xp.core.snapshot.SnapshotResults;
import com.enonic.wem.repo.internal.elasticsearch.document.DeleteDocument;
import com.enonic.wem.repo.internal.elasticsearch.document.StoreDocument;
import com.enonic.wem.repo.internal.elasticsearch.query.ElasticsearchQuery;
import com.enonic.wem.repo.internal.index.result.GetResult;
import com.enonic.wem.repo.internal.index.result.SearchResult;

public interface ElasticsearchDao
{
    public String store( IndexRequest indexRequest );

    public void store( Collection<StoreDocument> storeDocuments );

    public boolean delete( DeleteRequest deleteRequest );

    public boolean delete( DeleteDocument deleteDocument );

    public SearchResult find( ElasticsearchQuery query );

    public GetResult get( GetQuery getQuery );

    public long count( ElasticsearchQuery query );

    public SnapshotResult snapshot( final SnapshotParams params );

    public RestoreResult restoreSnapshot( final RestoreParams params );

    public SnapshotInfo getSnapshot( final String snapshotName );

    public void deleteSnapshot( final String snapshotName );

    public void deleteSnapshotRepository();

    public SnapshotResults listSnapshots();

}
