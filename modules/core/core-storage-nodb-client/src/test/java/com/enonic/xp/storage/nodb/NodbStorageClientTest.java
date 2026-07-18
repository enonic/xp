package com.enonic.xp.storage.nodb;

import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link NodbStorageClient#activate}'s config-validation logic. The
 * {@code configurationPolicy = REQUIRE} half of the selection mechanism (the component is
 * never instantiated at all without the PID) is a container-level guarantee this unit test
 * cannot exercise without a real OSGi/Declarative-Services runtime -- that is verified at
 * the boot-smoke level (Gate D), not here. What IS unit-testable, and covered below, is
 * that {@code activate} itself refuses to come up cleanly for a misconfigured PID rather
 * than silently doing something wrong, per {@link NodbStorageClient}'s class javadoc.
 */
class NodbStorageClientTest
{
    @Test
    void activate_backendNotNodb_throws()
    {
        final NodbStorageClient client = new NodbStorageClient();
        assertThrows( IllegalStateException.class, () -> client.activate( Map.of( "backend", "elasticsearch" ) ) );
    }

    @Test
    void activate_backendMissing_defaultsToElasticsearch_andThrows()
    {
        final NodbStorageClient client = new NodbStorageClient();
        assertThrows( IllegalStateException.class, () -> client.activate( Map.of() ) );
    }

    @Test
    void activate_nodbWithoutEndpoint_throws()
    {
        final NodbStorageClient client = new NodbStorageClient();
        assertThrows( IllegalStateException.class, () -> client.activate( Map.of( "backend", "nodb" ) ) );
    }

    @Test
    void activate_nodbWithMalformedEndpoint_throws()
    {
        final NodbStorageClient client = new NodbStorageClient();
        assertThrows( IllegalStateException.class,
                      () -> client.activate( Map.of( "backend", "nodb", "nodbEndpoint", "no-port-here" ) ) );
    }

    @Test
    void activate_nodbWithNonNumericPort_throws()
    {
        final NodbStorageClient client = new NodbStorageClient();
        assertThrows( IllegalStateException.class,
                      () -> client.activate( Map.of( "backend", "nodb", "nodbEndpoint", "localhost:notaport" ) ) );
    }

    @Test
    void activate_validNodbConfig_succeedsWithoutConnecting()
    {
        // gRPC channel construction never blocks on connectivity (see class javadoc's
        // unreachable-endpoint failure-mode note) -- this must succeed even though nothing
        // is listening on this port.
        final NodbStorageClient client = new NodbStorageClient();
        assertDoesNotThrow(
            () -> client.activate( Map.of( "backend", "nodb", "nodbEndpoint", "127.0.0.1:1", "nodbToken", "tok" ) ) );
        assertDoesNotThrow( client::deactivate );
    }
}
