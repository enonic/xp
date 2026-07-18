package com.enonic.xp.storage.nodb;

import java.util.Map;

import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.index.IndexType;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.storage.spi.IndexSettings;
import com.enonic.xp.storage.spi.StorageIndexExistsException;
import com.enonic.xp.storage.spi.StorageIndexNotFoundException;
import com.enonic.xp.storage.spi.UpdateIndexSettings;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link NodbRepositoryStorageAdmin} against an in-process stub server: repo lifecycle
 * status mapping (ALREADY_EXISTS/NOT_FOUND) and the documented no-op behavior of
 * refresh/updateSettings/putIndexMapping/getIndexSettings (nodb/BUILD-PHASE-1.md Gate 0
 * reconciliation table).
 */
class NodbRepositoryStorageAdminTest
{
    private static final RepositoryId REPO = RepositoryId.from( "myrepo" );

    private FakeNodbState state;

    private Server server;

    private ManagedChannel channel;

    private NodbRepositoryStorageAdmin admin;

    @BeforeEach
    void setUp()
        throws Exception
    {
        state = new FakeNodbState();
        final String serverName = "nodb-repo-admin-test-" + System.nanoTime();
        server = InProcessServerBuilder.forName( serverName )
            .directExecutor()
            .addService( new StubRepositoryAdminService( state ) )
            .build()
            .start();
        channel = InProcessChannelBuilder.forName( serverName ).directExecutor().build();
        admin = new NodbRepositoryStorageAdmin( new InProcessNodbStorageClient( channel ) );
    }

    @AfterEach
    void tearDown()
    {
        channel.shutdownNow();
        server.shutdownNow();
    }

    @Test
    void createIndex_thenIndexExists_true()
    {
        assertFalse( admin.indexExists( REPO ) );
        admin.createIndex( REPO, IndexSettings.from( Map.of() ), Map.of() );
        assertTrue( admin.indexExists( REPO ) );
    }

    @Test
    void createIndex_duplicate_throwsStorageIndexExistsException()
    {
        admin.createIndex( REPO, IndexSettings.from( Map.of() ), Map.of() );
        assertThrows( StorageIndexExistsException.class, () -> admin.createIndex( REPO, IndexSettings.from( Map.of() ), Map.of() ) );
    }

    @Test
    void deleteIndex_unknownRepo_throwsStorageIndexNotFoundException()
    {
        assertThrows( StorageIndexNotFoundException.class, () -> admin.deleteIndex( RepositoryId.from( "no-such-repo" ) ) );
    }

    @Test
    void deleteIndex_thenIndexExists_false()
    {
        admin.createIndex( REPO, IndexSettings.from( Map.of() ), Map.of() );
        admin.deleteIndex( REPO );
        assertFalse( admin.indexExists( REPO ) );
    }

    @Test
    void refreshUpdateSettingsPutMapping_areNoOps()
    {
        admin.createIndex( REPO, IndexSettings.from( Map.of() ), Map.of() );
        assertDoesNotThrow( () -> admin.refresh( REPO ) );
        assertDoesNotThrow( () -> admin.updateSettings( REPO, UpdateIndexSettings.from( "{}" ) ) );
        assertDoesNotThrow( () -> admin.putIndexMapping( REPO, IndexType.VERSION, Map.of() ) );
    }

    @Test
    void getIndexSettings_returnsEmptyMap()
    {
        assertTrue( admin.getIndexSettings( REPO, IndexType.VERSION ).isEmpty() );
    }
}
