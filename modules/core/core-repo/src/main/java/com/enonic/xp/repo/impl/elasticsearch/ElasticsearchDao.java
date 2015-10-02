package com.enonic.xp.repo.impl.elasticsearch;

import java.util.Collection;

import org.elasticsearch.snapshots.SnapshotInfo;

import com.enonic.xp.node.RestoreParams;
import com.enonic.xp.node.RestoreResult;
import com.enonic.xp.node.SnapshotParams;
import com.enonic.xp.node.SnapshotResult;
import com.enonic.xp.node.SnapshotResults;
import com.enonic.xp.repo.impl.elasticsearch.document.DeleteDocument;
import com.enonic.xp.repo.impl.elasticsearch.document.IndexDocument;
import com.enonic.xp.repo.impl.elasticsearch.query.ElasticsearchQuery;
import com.enonic.xp.repo.impl.search.result.SearchResult;


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
