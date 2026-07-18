package com.enonic.xp.storage.nodb;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;

import io.grpc.CallCredentials;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.config.ConfigInterpolator;
import com.enonic.xp.config.Configuration;
import com.enonic.nodb.proto.v1.NodeStoreGrpc;
import com.enonic.nodb.proto.v1.RepositoryAdminGrpc;

/**
 * Channel/stub lifecycle for the NoDB gRPC endpoint -- the one thing {@link NodbNodeStore}
 * and {@link NodbRepositoryStorageAdmin} both depend on. Configured via the
 * {@code com.enonic.xp.storage.nodb} PID (file {@code com.enonic.xp.storage.nodb.cfg}
 * under XP_HOME/config):
 * <pre>
 *   backend       elasticsearch|nodb, default elasticsearch (safety default: even if this
 *                 PID is provisioned, an explicit non-"nodb" value keeps the client dark)
 *   nodbEndpoint  host:port of the NoDB server (required when backend=nodb)
 *   nodbToken     bearer token attached to every call (${env.VAR}/${sys.prop}
 *                 interpolation supported via ConfigInterpolator, so it need not be
 *                 plaintext in the .cfg file)
 * </pre>
 * <p>
 * <b>Selection mechanism (nodb/BUILD-PHASE-1.md Gate B):</b> {@code configurationPolicy =
 * REQUIRE} means this component is never even instantiated unless the PID has been
 * explicitly provisioned -- a default boot with no {@code com.enonic.xp.storage.nodb.cfg}
 * file present never activates it, so {@link NodbNodeStore}/{@link
 * NodbRepositoryStorageAdmin} (which {@code @Reference} this class) never activate either,
 * and the elasticsearch-backed {@code NodeStore}/{@code RepositoryStorageAdmin} components
 * (Phase 0, {@code storage.backend=elasticsearch}) remain the only registered providers --
 * a config-less boot is byte-identical to today. When the PID IS provisioned with
 * {@code backend=nodb}, {@link NodbNodeStore}/{@link NodbRepositoryStorageAdmin} register
 * with a higher {@code service.ranking} than their elasticsearch counterparts, so existing
 * plain {@code @Reference NodeStore}/{@code @Reference RepositoryStorageAdmin} consumers in
 * core-repo (unmodified by this module) rebind to the nodb-backed services automatically --
 * standard Declarative Services behavior for a static reference when a higher-ranked target
 * appears, no core-repo edits required.
 * <p>
 * <b>Unreachable-endpoint failure mode:</b> gRPC channel construction
 * ({@link ManagedChannelBuilder#build()}) never blocks on connectivity, so this component
 * activates successfully (and the nodb services register/rebind) even if the configured
 * endpoint is unreachable. The failure surfaces at the FIRST real RPC call, as an
 * {@link NodbClientException} wrapping the gRPC {@code UNAVAILABLE} status -- fail fast, no
 * retry loop, no automatic fallback to elasticsearch (Phase 1 policy). Operators pointing a
 * boot at nodb therefore need the endpoint reachable before first repo access (e.g. system
 * repo init at boot), or boot itself fails loudly rather than silently degrading.
 * <p>
 * <b>Token scope note:</b> {@code nodb/server/.../auth/TenantAuthInterceptor} requires
 * {@code operator} scope for {@code RepositoryAdmin.CreateRepository}/{@code
 * DeleteRepository} -- which is exactly what {@link NodbRepositoryStorageAdmin#createIndex}/
 * {@link NodbRepositoryStorageAdmin#deleteIndex} map onto. XP's own runtime calls those as
 * part of ordinary operation (e.g. project/repository creation), not only human/CI-driven
 * management -- so {@code nodbToken} MUST be minted with {@code operator} scope (see
 * {@code NodbTokenTool --scope operator}), not {@code runtime}, or repo lifecycle
 * operations fail with {@code PERMISSION_DENIED}. Flagged here since it is a real tension
 * with the two-scope model's original runtime-vs-operator intent, not merely a config
 * detail.
 */
@Component(configurationPid = "com.enonic.xp.storage.nodb", configurationPolicy = ConfigurationPolicy.REQUIRE,
    service = NodbStorageClient.class)
public class NodbStorageClient
{
    private static final String BACKEND_NODB = "nodb";

    private ManagedChannel channel;

    private NodeStoreGrpc.NodeStoreBlockingStub nodeStoreStub;

    private RepositoryAdminGrpc.RepositoryAdminBlockingStub repositoryAdminStub;

    @Activate
    public void activate( final Map<String, String> properties )
    {
        final Configuration config = new ConfigInterpolator().interpolate(
            ConfigBuilder.create().load( getClass(), "default.properties" ).addAll( properties ).build() );

        final String backend = config.get( "backend" );
        if ( !BACKEND_NODB.equals( backend ) )
        {
            // Config PID provisioned but not opted into nodb (e.g. left at the
            // "elasticsearch" default, or explicitly set back) -- refuse to activate
            // rather than silently doing nothing useful. SCR treats an exception thrown
            // from @Activate as a failed activation: the service is not registered.
            throw new IllegalStateException(
                "com.enonic.xp.storage.nodb is configured but backend=[" + backend + "] (expected [" + BACKEND_NODB +
                    "]); NoDB client not activated." );
        }

        final String endpoint = config.get( "nodbEndpoint" );
        if ( endpoint == null || endpoint.isBlank() )
        {
            throw new IllegalStateException( "com.enonic.xp.storage.nodb: backend=nodb requires nodbEndpoint (host:port)" );
        }
        final int colon = endpoint.lastIndexOf( ':' );
        if ( colon <= 0 || colon == endpoint.length() - 1 )
        {
            throw new IllegalStateException( "com.enonic.xp.storage.nodb: nodbEndpoint [" + endpoint + "] is not in host:port form" );
        }
        final String host = endpoint.substring( 0, colon );
        final int port;
        try
        {
            port = Integer.parseInt( endpoint.substring( colon + 1 ) );
        }
        catch ( NumberFormatException e )
        {
            throw new IllegalStateException( "com.enonic.xp.storage.nodb: nodbEndpoint [" + endpoint + "] has a non-numeric port", e );
        }

        final String token = config.get( "nodbToken" );

        // Phase 1 local posture (nodb/DESIGN.md §7): plaintext, no mTLS/TLS story yet --
        // matches nodb/client-java's NodbClient and the dev issuer's own posture. Channel
        // construction does not connect eagerly (see class javadoc's failure-mode note).
        this.channel = ManagedChannelBuilder.forAddress( host, port ).usePlaintext().build();

        final NodeStoreGrpc.NodeStoreBlockingStub baseNodeStore = NodeStoreGrpc.newBlockingStub( channel );
        final RepositoryAdminGrpc.RepositoryAdminBlockingStub baseAdmin = RepositoryAdminGrpc.newBlockingStub( channel );

        if ( token == null || token.isBlank() )
        {
            this.nodeStoreStub = baseNodeStore;
            this.repositoryAdminStub = baseAdmin;
        }
        else
        {
            final CallCredentials credentials = new BearerTokenCallCredentials( token );
            this.nodeStoreStub = baseNodeStore.withCallCredentials( credentials );
            this.repositoryAdminStub = baseAdmin.withCallCredentials( credentials );
        }
    }

    @Deactivate
    public void deactivate()
    {
        if ( channel == null )
        {
            return;
        }
        channel.shutdown();
        try
        {
            if ( !channel.awaitTermination( 5, TimeUnit.SECONDS ) )
            {
                channel.shutdownNow();
            }
        }
        catch ( InterruptedException e )
        {
            channel.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    NodeStoreGrpc.NodeStoreBlockingStub nodeStore()
    {
        return nodeStoreStub;
    }

    RepositoryAdminGrpc.RepositoryAdminBlockingStub repositoryAdmin()
    {
        return repositoryAdminStub;
    }
}
