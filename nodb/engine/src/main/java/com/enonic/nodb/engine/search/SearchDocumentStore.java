package com.enonic.nodb.engine.search;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * {@code search_document} (migration 003): the XP-shipped index documents, one row per
 * (repo, branch, node).
 *
 * <p>This table is what makes decision 3 ("XP keeps building index documents and ships them")
 * compatible with §3.3's asynchronous indexer. Without it the outbox would be a list of
 * "something changed" notes with nothing to apply, {@code refresh(SEARCH)} could not be honoured
 * across a restart, and Gate G's rebuild drill would have nothing to replay. It becomes a cache
 * — and can be dropped in its own migration — when decision 3's later swap derives documents
 * server-side from {@code payload}.
 *
 * <p>Rows hold the CANONICAL document, never the projected one: see {@link SearchDocument}.
 */
public final class SearchDocumentStore
{
    private SearchDocumentStore()
    {
    }

    /**
     * Upserts one document. {@code (repo_key, branch, node_id)} is the PK, so re-shipping a node
     * replaces it — the same "last write wins per node per branch" semantics as
     * {@code branch_entry}, and the same semantics as an OpenSearch bulk {@code index}.
     */
    public static void store( Connection connection, long repoKey, String branch, SearchDocument document )
        throws SQLException
    {
        try (PreparedStatement statement = connection.prepareStatement( """
            INSERT INTO search_document (repo_key, branch, node_id, doc, analyzer)
            VALUES (?, ?, ?, ?::jsonb, ?)
            ON CONFLICT (repo_key, branch, node_id) DO UPDATE
                SET doc = EXCLUDED.doc, analyzer = EXCLUDED.analyzer, ts = now()
            """ ))
        {
            statement.setLong( 1, repoKey );
            statement.setString( 2, branch );
            statement.setString( 3, document.nodeId() );
            statement.setString( 4, toJsonString( document ) );
            statement.setString( 5, document.analyzer() );
            statement.executeUpdate();
        }
    }

    /**
     * Drops every stored document of a repository, all branches. Phase 4 Gate F: this is the other
     * half of purging a repository's search index ({@code IndexService.reindex(initialize = true)},
     * XP's own "rebuild from scratch"). Dropping only the OpenSearch index would leave the shipped
     * documents behind, and a later rebuild-from-documents would resurrect exactly the branches the
     * purge removed -- the same trap Gate A recorded for per-op deletes.
     */
    public static int deleteAll( Connection connection, long repoKey )
        throws SQLException
    {
        try (PreparedStatement statement = connection.prepareStatement( "DELETE FROM search_document WHERE repo_key = ?" ))
        {
            statement.setLong( 1, repoKey );
            return statement.executeUpdate();
        }
    }

    public static void delete( Connection connection, long repoKey, String branch, Collection<String> nodeIds )
        throws SQLException
    {
        if ( nodeIds.isEmpty() )
        {
            return;
        }
        try (PreparedStatement statement = connection.prepareStatement(
            "DELETE FROM search_document WHERE repo_key = ? AND branch = ? AND node_id = ANY(?)" ))
        {
            statement.setLong( 1, repoKey );
            statement.setString( 2, branch );
            statement.setArray( 3, connection.createArrayOf( "text", nodeIds.toArray( new String[0] ) ) );
            statement.executeUpdate();
        }
    }

    /**
     * Bulk read for the indexer: the documents for a set of node ids in one branch.
     *
     * <p>Missing ids are simply absent — the same convention as {@code getBranchEntries}. That is
     * not laxity, it is the ordering contract: {@code WriteBatch} commits its own INDEX outbox
     * row for a branch-entry change, and XP ships the document in a SEPARATE call afterwards
     * (the SPI has always had two calls: {@code NodeStore.storeNode} then
     * {@code NodeSearchIndex.index}). So the indexer legitimately reaches a seq whose document
     * has not arrived yet, and must skip it rather than delete the node from the index. The
     * document's own outbox row re-applies it moments later, and {@code awaitRefresh} on THAT
     * seq is the read-your-writes barrier XP actually waits on.
     */
    public static Map<String, SearchDocument> get( Connection connection, long repoKey, String branch, Collection<String> nodeIds )
        throws SQLException
    {
        if ( nodeIds.isEmpty() )
        {
            return Map.of();
        }
        try (PreparedStatement statement = connection.prepareStatement(
            "SELECT node_id, doc::text, analyzer FROM search_document WHERE repo_key = ? AND branch = ? AND node_id = ANY(?)" ))
        {
            statement.setLong( 1, repoKey );
            statement.setString( 2, branch );
            statement.setArray( 3, connection.createArrayOf( "text", nodeIds.toArray( new String[0] ) ) );
            return readAll( statement );
        }
    }

    /**
     * Every document of one repo, ordered — the rebuild-from-docs replay source (Gate G's drill,
     * and this gate's {@code rebuildFromDocumentsProducesIdenticalIndex} test).
     */
    public static List<BranchDocument> listAll( Connection connection, long repoKey )
        throws SQLException
    {
        try (PreparedStatement statement = connection.prepareStatement(
            "SELECT branch, node_id, doc::text, analyzer FROM search_document WHERE repo_key = ? ORDER BY branch, node_id" ))
        {
            statement.setLong( 1, repoKey );
            List<BranchDocument> documents = new ArrayList<>();
            try (ResultSet resultSet = statement.executeQuery())
            {
                while ( resultSet.next() )
                {
                    documents.add( new BranchDocument( resultSet.getString( 1 ), read( resultSet, 2, 3, 4 ) ) );
                }
            }
            return List.copyOf( documents );
        }
    }

    private static Map<String, SearchDocument> readAll( PreparedStatement statement )
        throws SQLException
    {
        Map<String, SearchDocument> documents = new LinkedHashMap<>();
        try (ResultSet resultSet = statement.executeQuery())
        {
            while ( resultSet.next() )
            {
                SearchDocument document = read( resultSet, 1, 2, 3 );
                documents.put( document.nodeId(), document );
            }
        }
        return Map.copyOf( documents );
    }

    private static SearchDocument read( ResultSet resultSet, int nodeIdColumn, int docColumn, int analyzerColumn )
        throws SQLException
    {
        String nodeId = resultSet.getString( nodeIdColumn );
        String json = resultSet.getString( docColumn );
        String analyzer = resultSet.getString( analyzerColumn );
        try
        {
            return SearchDocument.fromJson( nodeId, analyzer, OpenSearchClient.mapper().readTree( json ) );
        }
        catch ( JsonProcessingException e )
        {
            throw new IllegalStateException( "Corrupt search_document row for node " + nodeId, e );
        }
    }

    private static String toJsonString( SearchDocument document )
    {
        return document.toJson().toString();
    }

    /** One replayable document plus the branch it belongs to. */
    public record BranchDocument(String branch, SearchDocument document)
    {
    }
}
