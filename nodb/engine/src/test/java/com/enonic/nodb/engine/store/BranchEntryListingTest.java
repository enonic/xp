package com.enonic.nodb.engine.store;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.enonic.nodb.engine.TenantContext;
import com.enonic.nodb.engine.TenantProvisioner;
import com.enonic.nodb.engine.Tx;
import com.enonic.nodb.engine.model.BranchEntryPage;
import com.enonic.nodb.engine.model.BranchEntryRecord;
import com.enonic.nodb.engine.model.VersionRecord;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Phase 4 decision D2 (nodb/BUILD-PHASE-4.md): the branch-entry LISTING surface — the storage
 * answer to what {@code DeleteNodeCommand}, {@code RepositoryServiceImpl#deleteBranch} and
 * {@code ReindexExecutor} used to ask the ES {@code storage-<repo>} index.
 *
 * <p>What is worth testing here is not "does a SELECT return rows" but the four properties a
 * keyset walk can get wrong and a smoke test would not notice: the subtree predicate's boundary
 * (the prefix row itself excluded, a sibling whose name merely STARTS WITH the prefix excluded),
 * LIKE-metacharacter escaping in paths, that a paged walk yields each row exactly once in the
 * requested order, and that the up-front total is paid for once rather than per page.
 */
@Testcontainers
class BranchEntryListingTest
{
    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>( "postgres:17" );

    private static final TenantContext ACME = new TenantContext( "acme" );

    private static final TenantContext FISK = new TenantContext( "fisk" );

    private static final String DRAFT = "draft";

    private static final String MASTER = "master";

    private static HikariDataSource dataSource;

    private static long repoKey;

    private static long otherRepoKey;

    @BeforeAll
    static void setUp()
        throws SQLException
    {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl( POSTGRES.getJdbcUrl() );
        config.setUsername( POSTGRES.getUsername() );
        config.setPassword( POSTGRES.getPassword() );
        config.setMaximumPoolSize( 8 );
        dataSource = new HikariDataSource( config );

        TenantProvisioner provisioner = new TenantProvisioner( dataSource, POSTGRES.getUsername() );
        provisioner.provision( ACME );
        provisioner.provision( FISK );

        repoKey = createRepo( ACME, "listing-repo" );
        otherRepoKey = createRepo( ACME, "listing-repo-other" );
        createRepo( FISK, "listing-repo" );

        // The delete-cascade subtree, plus three deliberate near-misses:
        //   /content-sibling   -- starts with "/content" but is NOT below it
        //   /content_x/leaf    -- an underscore, which is a LIKE single-char wildcard
        //   /Content/Upper     -- differs from the scope only in case
        seed( ACME, repoKey, DRAFT, "n-root", "/" );
        seed( ACME, repoKey, DRAFT, "n-content", "/content" );
        seed( ACME, repoKey, DRAFT, "n-a", "/content/a" );
        seed( ACME, repoKey, DRAFT, "n-b", "/content/b" );
        seed( ACME, repoKey, DRAFT, "n-a-deep", "/content/a/deep" );
        seed( ACME, repoKey, DRAFT, "n-upper", "/Content/Upper" );
        seed( ACME, repoKey, DRAFT, "n-sibling", "/content-sibling" );
        seed( ACME, repoKey, DRAFT, "n-underscore", "/content_x/leaf" );
        seed( ACME, repoKey, MASTER, "n-master-only", "/content/master-only" );
        seed( ACME, otherRepoKey, DRAFT, "n-other-repo", "/content/other-repo" );
        seed( FISK, repoKey, DRAFT, "n-other-tenant", "/content/other-tenant" );
    }

    @AfterAll
    static void tearDown()
    {
        dataSource.close();
    }

    // ---- the delete cascade's subtree ------------------------------------------------------

    /**
     * The exact shape {@code DeleteNodeCommand} needs: every descendant of the deleted node,
     * deepest first, so a child is always deleted before its parent. Also the boundary cases —
     * the prefix row itself, the {@code /content-sibling} near-miss and the other branch's row
     * must all be absent.
     */
    @Test
    void subtreeListingIsPathDescendingAndExcludesThePrefixRowItself()
        throws SQLException
    {
        assertEquals( List.of( "/Content/Upper", "/content/b", "/content/a/deep", "/content/a" ), paths( list( "/content", true, 100 ) ) );
    }

    /** Case-insensitive, like the lowercased ES path field and {@code NodePath} equality. */
    @Test
    void subtreePrefixIsCaseInsensitive()
        throws SQLException
    {
        assertEquals( paths( list( "/content", true, 100 ) ), paths( list( "/CONTENT", true, 100 ) ) );
    }

    /**
     * {@code _} is a single-character wildcard in LIKE, so an unescaped prefix would make
     * {@code /content_x/leaf} a child of {@code /content}. It is not — and the sibling test above
     * would not catch this, since only the underscore path is affected.
     */
    @Test
    void likeMetacharactersInAPathAreEscapedRatherThanMatchedAsWildcards()
        throws SQLException
    {
        assertEquals( List.of( "/content_x/leaf" ), paths( list( "/content_x", true, 100 ) ) );
        assertFalse( paths( list( "/content", true, 100 ) ).contains( "/content_x/leaf" ) );
    }

    @Test
    void aSubtreeWithNoDescendantsIsEmptyWithATotalOfZero()
        throws SQLException
    {
        BranchEntryPage page = list( "/content/b", true, 100 );
        assertTrue( page.entries().isEmpty() );
        assertEquals( 0, page.totalHits() );
        assertFalse( page.hasMore() );
    }

    // ---- the whole branch -----------------------------------------------------------------

    @Test
    void wholeBranchListingIsPathAscendingAndScopedToOneBranchRepoAndTenant()
        throws SQLException
    {
        // Byte ('"'"'C'"'"') collation, matching the lowercased ES path keyword: '"'"'-'"'"' (0x2D) < '"'"'/'"'"' (0x2F) < '"'"'_'"'"' (0x5F),
        // and a strict prefix always precedes its extensions.
        assertEquals( List.of( "/", "/content", "/content-sibling", "/content/a", "/content/a/deep", "/content/b", "/Content/Upper",
                               "/content_x/leaf" ), paths( list( null, false, 100 ) ) );
        assertEquals( List.of( "/content/master-only" ), paths( list( ACME, repoKey, MASTER, null, false, 100 ) ) );
        assertEquals( List.of( "/content/other-repo" ), paths( list( ACME, otherRepoKey, DRAFT, null, false, 100 ) ) );
        assertEquals( List.of( "/content/other-tenant" ), paths( list( FISK, repoKey, DRAFT, null, false, 100 ) ) );
    }

    // ---- keyset paging --------------------------------------------------------------------

    /**
     * A page size of 1 over a walk of 8 is the paging worst case: it maximizes the number of
     * cursor hand-offs, so a keyset that is off by one boundary either drops or repeats a row.
     * Asserted against the single-page result, in order, for both directions.
     */
    @Test
    void aKeysetWalkYieldsExactlyTheSingleShotResultInOrderAtEveryPageSize()
        throws SQLException
    {
        for ( boolean descending : new boolean[]{true, false} )
        {
            List<String> expected = paths( list( null, descending, 100 ) );
            for ( int pageSize : new int[]{1, 2, 3, 7, 8, 9} )
            {
                assertEquals( expected, walk( null, descending, pageSize ),
                              "descending=" + descending + " pageSize=" + pageSize );
            }
        }
    }

    @Test
    void onlyTheFirstPageCarriesTheTotalAndHasMoreIsAnswredWithoutATrailingEmptyPage()
        throws SQLException
    {
        BranchEntryPage first = list( null, false, 3 );
        assertEquals( 8, first.totalHits() );
        assertTrue( first.hasMore() );

        BranchEntryPage second = page( null, false, 3, first.nextAfterPath(), first.nextAfterNodeId() );
        assertEquals( BranchEntryPage.NO_TOTAL, second.totalHits() );
        assertTrue( second.hasMore() );

        BranchEntryPage third = page( null, false, 3, second.nextAfterPath(), second.nextAfterNodeId() );
        assertEquals( 2, third.entries().size() );
        // Nothing left, and the walk learns it from THIS page rather than from a fourth round trip.
        assertFalse( third.hasMore() );
    }

    /** The joined payload hashes must survive the listing select, exactly as on every other read. */
    @Test
    void listedEntriesCarryTheJoinedPayloadHashes()
        throws SQLException
    {
        for ( BranchEntryRecord entry : list( "/content", true, 100 ).entries() )
        {
            assertTrue( entry.nodeDataHash().startsWith( "sha256:" ), entry.nodePath() );
            assertTrue( entry.indexConfigHash().startsWith( "sha256:" ), entry.nodePath() );
            assertTrue( entry.aclHash().startsWith( "sha256:" ), entry.nodePath() );
        }
    }

    // ---- helpers -------------------------------------------------------------------------

    private static List<String> walk( String pathPrefix, boolean descending, int pageSize )
        throws SQLException
    {
        List<String> result = new ArrayList<>();
        BranchEntryPage page = list( ACME, repoKey, DRAFT, pathPrefix, descending, pageSize );
        while ( true )
        {
            page.entries().forEach( entry -> result.add( entry.nodePath() ) );
            if ( !page.hasMore() )
            {
                return result;
            }
            page = page( pathPrefix, descending, pageSize, page.nextAfterPath(), page.nextAfterNodeId() );
        }
    }

    private static BranchEntryPage list( String pathPrefix, boolean descending, int pageSize )
        throws SQLException
    {
        return list( ACME, repoKey, DRAFT, pathPrefix, descending, pageSize );
    }

    private static BranchEntryPage list( TenantContext tenant, long repo, String branch, String pathPrefix, boolean descending,
                                          int pageSize )
        throws SQLException
    {
        return Tx.inTenantTx( dataSource, tenant,
                               connection -> BranchStore.listEntries( connection, repo, branch, pathPrefix, descending, "", "", pageSize,
                                                                       true ) );
    }

    private static BranchEntryPage page( String pathPrefix, boolean descending, int pageSize, String afterPath, String afterNodeId )
        throws SQLException
    {
        return Tx.inTenantTx( dataSource, ACME,
                               connection -> BranchStore.listEntries( connection, repoKey, DRAFT, pathPrefix, descending, afterPath,
                                                                       afterNodeId, pageSize, false ) );
    }

    private static List<String> paths( BranchEntryPage page )
    {
        return page.entries().stream().map( BranchEntryRecord::nodePath ).toList();
    }

    private static long createRepo( TenantContext tenant, String repoId )
        throws SQLException
    {
        return Tx.inTenantSchema( dataSource, tenant, connection -> RepositoryLifecycle.createRepository( connection, repoId, null ) );
    }

    private static void seed( TenantContext tenant, long repo, String branch, String nodeId, String nodePath )
        throws SQLException
    {
        String versionId = "v-" + nodeId + "-" + branch;
        Tx.inTenantTx( dataSource, tenant, connection -> {
            String dataHash = PayloadStore.putPayload( connection, ( "data-" + versionId ).getBytes( StandardCharsets.UTF_8 ) );
            String indexHash = PayloadStore.putPayload( connection, ( "index-" + versionId ).getBytes( StandardCharsets.UTF_8 ) );
            String aclHash = PayloadStore.putPayload( connection, ( "acl-" + versionId ).getBytes( StandardCharsets.UTF_8 ) );
            VersionStore.store( connection, repo,
                                 new VersionRecord( versionId, nodeId, nodePath, Instant.ofEpochMilli( 1_000 ), dataHash, indexHash,
                                                     aclHash, List.of(), null, Map.of() ) );
            BranchStore.store( connection, repo,
                                new BranchEntryRecord( branch, nodeId, versionId, nodePath, Instant.ofEpochMilli( 1_000 ) ) );
            return null;
        } );
    }
}
