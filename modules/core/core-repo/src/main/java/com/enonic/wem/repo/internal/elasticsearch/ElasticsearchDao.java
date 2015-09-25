package com.enonic.wem.repo.internal.elasticsearch;

import java.util.Collection;

import org.elasticsearch.snapshots.SnapshotInfo;

import com.enonic.wem.repo.internal.elasticsearch.document.DeleteDocument;
import com.enonic.wem.repo.internal.elasticsearch.document.IndexDocument;
import com.enonic.wem.repo.internal.elasticsearch.query.ElasticsearchQuery;
import com.enonic.wem.repo.internal.search.result.SearchResult;
import com.enonic.xp.node.RestoreParams;
import com.enonic.xp.node.RestoreResult;
import com.enonic.xp.node.SnapshotParams;
import com.enonic.xp.node.SnapshotResult;
import com.enonic.xp.node.SnapshotResults;


public interface ElasticsearchDao
{
    void store( Collection<IndexDocument> indexDocuments );

    boolean delete( DeleteDocument deleteDocument );

    SearchResult search( ElasticsearchQuery query );

    long count( ElasticsearchQuery query );

    SnapshotResult snapshot( final SnapshotParams params );

    RestoreResult restoreSnapshot( final RestoreParams params );

    SnapshotInfo getSnapshot( final String snapshotName );

    void deleteSnapshot( final String snapshotName );

    void deleteSnapshotRepository();

    SnapshotResults listSnapshots();

}
