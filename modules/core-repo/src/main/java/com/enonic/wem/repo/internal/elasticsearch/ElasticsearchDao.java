package com.enonic.wem.repo.internal.elasticsearch;

import java.util.Collection;

import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.snapshots.SnapshotInfo;

import com.enonic.wem.repo.internal.elasticsearch.document.DeleteDocument;
import com.enonic.wem.repo.internal.elasticsearch.document.IndexDocument;
import com.enonic.wem.repo.internal.elasticsearch.query.ElasticsearchQuery;
import com.enonic.wem.repo.internal.storage.result.SearchResult;
import com.enonic.xp.snapshot.RestoreParams;
import com.enonic.xp.snapshot.RestoreResult;
import com.enonic.xp.snapshot.SnapshotParams;
import com.enonic.xp.snapshot.SnapshotResult;
import com.enonic.xp.snapshot.SnapshotResults;

public interface ElasticsearchDao
{
    void store( Collection<IndexDocument> indexDocuments );

    boolean delete( DeleteRequest deleteRequest );

    boolean delete( DeleteDocument deleteDocument );

    SearchResult find( ElasticsearchQuery query );

    long count( ElasticsearchQuery query );

    SnapshotResult snapshot( final SnapshotParams params );

    RestoreResult restoreSnapshot( final RestoreParams params );

    SnapshotInfo getSnapshot( final String snapshotName );

    void deleteSnapshot( final String snapshotName );

    void deleteSnapshotRepository();

    SnapshotResults listSnapshots();

}
