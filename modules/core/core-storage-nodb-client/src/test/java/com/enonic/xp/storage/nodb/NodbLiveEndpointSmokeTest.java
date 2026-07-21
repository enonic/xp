package com.enonic.xp.storage.nodb;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.storage.spi.BranchEntryRecord;
import com.enonic.xp.storage.spi.IndexSettings;
import com.enonic.xp.storage.spi.NodeSegments;
import com.enonic.xp.storage.spi.PayloadSegment;
import com.enonic.xp.storage.spi.VersionRecord;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Opt-in end-to-end smoke test against a REAL, already-running NoDB server -- not part of
 * the default test run (no Postgres/nodb-server dependency is wired into this module's
 * build, per this module's pragmatic test-approach choice: unit tests run against an
 * in-process stub, see {@link FakeNodbState}'s javadoc).
 * <p>
 * To run: start a real NoDB server (see {@code nodb/README} / {@code nodb dev}), mint an
 * OPERATOR-scope token (this test calls {@code createIndex}/{@code deleteIndex}, which
 * require operator scope -- see {@link NodbStorageClient}'s "Token scope note"):
 * <pre>
 *   java -cp ... com.enonic.nodb.server.auth.NodbTokenTool --tenant t1 --scope operator
 * </pre>
 * then run with:
 * <pre>
 *   ./gradlew :core:core-storage-nodb-client:test \
 *     -Dxp.itest.nodb.endpoint=localhost:7070 \
 *     -Dxp.itest.nodb.token=&lt;token&gt;
 * </pre>
 * Absent {@code xp.itest.nodb.endpoint}, this entire class is skipped (JUnit5
 * {@code @EnabledIfSystemProperty}) -- it never runs as part of the normal build/gate.
 */
@EnabledIfSystemProperty(named = "xp.itest.nodb.endpoint", matches = ".+")
class NodbLiveEndpointSmokeTest
{
    @Test
    void createRepoStoreAndReadNodeRoundTrip()
    {
        final String endpoint = System.getProperty( "xp.itest.nodb.endpoint" );
        final String token = System.getProperty( "xp.itest.nodb.token" );
        final int colon = endpoint.lastIndexOf( ':' );
        final String host = endpoint.substring( 0, colon );
        final int port = Integer.parseInt( endpoint.substring( colon + 1 ) );

        final NodbStorageClient client = new NodbStorageClient();
        client.activate( token == null
                              ? Map.of( "backend", "nodb", "nodbEndpoint", endpoint )
                              : Map.of( "backend", "nodb", "nodbEndpoint", endpoint, "nodbToken", token ) );
        try
        {
            final NodbRepositoryStorageAdmin admin = new NodbRepositoryStorageAdmin( client );
            final NodbNodeStore nodeStore = new NodbNodeStore( client );
            final RepositoryId repo = RepositoryId.from( "smoke-" + System.currentTimeMillis() );

            admin.createIndex( repo, IndexSettings.from( Map.of() ), Map.of() );
            try
            {
                final Instant now = Instant.now();
                // Phase 3 Gate B (nodb/BUILD-PHASE-3.md): the re-added node_version payload
                // FK requires all three hashes to reference a stored payload row -- inline
                // segment bytes for all three (not null), matching each declared hash.
                final NodeSegments segments =
                    new NodeSegments( new PayloadSegment( "sha256:smoke-data", "smoke node data".getBytes() ),
                                       new PayloadSegment( "sha256:smoke-idx", "smoke index config".getBytes() ),
                                       new PayloadSegment( "sha256:smoke-acl", "smoke acl".getBytes() ) );
                final VersionRecord version =
                    new VersionRecord( "smoke-v1", "smoke-n1", "/smoke", now, "sha256:smoke-data", "sha256:smoke-idx", "sha256:smoke-acl",
                                        List.of(), null, null );
                nodeStore.storeVersion( repo, version, segments );
                nodeStore.storeBranchEntry( repo, Branch.from( "master" ),
                                             new BranchEntryRecord( "smoke-n1", "/smoke", "smoke-v1", "sha256:smoke-data",
                                                                     "sha256:smoke-idx", "sha256:smoke-acl", now ) );

                final BranchEntryRecord fetched = nodeStore.getBranchEntry( repo, Branch.from( "master" ), "smoke-n1", null );
                assertNotNull( fetched );
                assertEquals( "smoke-v1", fetched.versionId() );
            }
            finally
            {
                admin.deleteIndex( repo );
            }
        }
        finally
        {
            client.deactivate();
        }
    }
}
