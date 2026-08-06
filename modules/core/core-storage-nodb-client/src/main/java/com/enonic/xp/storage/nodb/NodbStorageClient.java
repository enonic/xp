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
import com.enonic.nodb.proto.v1.BinariesGrpc;
import com.enonic.nodb.proto.v1.NodeSearchGrpc;
import com.enonic.nodb.proto.v1.NodeStoreGrpc;
import com.enonic.nodb.proto.v1.RepositoryAdminGrpc;

/**
 * Channel/stub lifecycle for the NoDB gRPC endpoint -- the one thing {@link NodbNodeStore},
 * {@link NodbRepositoryStorageAdmin}, and (Phase 2 Gate B) {@link NodbBinaryBlobStore} all
 * depend on. Configured via the {@code com.enonic.xp.storage.nodb} PID (file
 * {@code com.enonic.xp.storage.nodb.cfg} under XP_HOME/config):
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
 * NodbRepositoryStorageAdmin}/{@link NodbBinaryBlobStore} (which all {@code @Reference} this
 * class) never activate either, and the elasticsearch-backed {@code NodeStore}/
 * {@code RepositoryStorageAdmin} components plus the plain file/S3 {@code BlobStore}
 * (Phase 0, {@code storage.backend=elasticsearch}) remain the only registered providers --
 * a config-less boot is byte-identical to today. When the PID IS provisioned with
 * {@code backend=nodb}, {@link NodbNodeStore}/{@link NodbRepositoryStorageAdmin}/
 * {@link NodbBinaryBlobStore} register with a higher {@code service.ranking} than their
 * defaults, so existing plain {@code @Reference NodeStore}/
 * {@code @Reference RepositoryStorageAdmin}/{@code @Reference BlobStore} consumers in
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
 * <b>Token scope:</b> {@code nodbToken} is minted with {@code runtime} scope (see
 * {@code NodbTokenTool --scope runtime}). Repository lifecycle (create/delete) is
 * RUNTIME-scoped server-side per the Phase 1 gate-B scope-model correction: in XP,
 * repo lifecycle within a tenant is an ordinary runtime operation (content projects
 * create repos from app code), and intra-tenant authorization is the runtime's job
 * under the two-layer model. Operator scope guards TENANT-level operations only
 * (dump/load, snapshots, bulk transfer — when implemented).
 */
@Component(configurationPid = "com.enonic.xp.storage.nodb", configurationPolicy = ConfigurationPolicy.REQUIRE,
    service = NodbStorageClient.class)
public class NodbStorageClient
{
    private static final String BACKEND_NODB = "nodb";

    private ManagedChannel channel;

    private NodeStoreGrpc.NodeStoreBlockingStub nodeStoreStub;

    private NodeSearchGrpc.NodeSearchBlockingStub nodeSearchStub;

    private RepositoryAdminGrpc.RepositoryAdminBlockingStub repositoryAdminStub;

    // Two stub flavors for the Binaries service (Phase 2 Gate B): GetBinary/BinaryExists/
    // DeleteBinary are unary/server-streaming, served fine by a blocking stub (same as
    // NodeStore's streaming methods, e.g. getBranchEntries). PutBinary is CLIENT-streaming
    // though -- grpc-java's blocking stub flavor does not support that call shape at all
    // (only unary + server-streaming); it requires the async stub, driven via a
    // StreamObserver<PutBinaryChunk> (see NodbBinaryBlobStore, which bridges that back to a
    // blocking call from XP's perspective to preserve the binaries-before-commit invariant).
    private BinariesGrpc.BinariesBlockingStub binariesBlockingStub;

    private BinariesGrpc.BinariesStub binariesAsyncStub;

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
        final NodeSearchGrpc.NodeSearchBlockingStub baseNodeSearch = NodeSearchGrpc.newBlockingStub( channel );
        final RepositoryAdminGrpc.RepositoryAdminBlockingStub baseAdmin = RepositoryAdminGrpc.newBlockingStub( channel );
        final BinariesGrpc.BinariesBlockingStub baseBinariesBlocking = BinariesGrpc.newBlockingStub( channel );
        final BinariesGrpc.BinariesStub baseBinariesAsync = BinariesGrpc.newStub( channel );

        if ( token == null || token.isBlank() )
        {
            this.nodeStoreStub = baseNodeStore;
            this.nodeSearchStub = baseNodeSearch;
            this.repositoryAdminStub = baseAdmin;
            this.binariesBlockingStub = baseBinariesBlocking;
            this.binariesAsyncStub = baseBinariesAsync;
        }
        else
        {
            final CallCredentials credentials = new BearerTokenCallCredentials( token );
            this.nodeStoreStub = baseNodeStore.withCallCredentials( credentials );
            this.nodeSearchStub = baseNodeSearch.withCallCredentials( credentials );
            this.repositoryAdminStub = baseAdmin.withCallCredentials( credentials );
            this.binariesBlockingStub = baseBinariesBlocking.withCallCredentials( credentials );
            this.binariesAsyncStub = baseBinariesAsync.withCallCredentials( credentials );
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

    NodeSearchGrpc.NodeSearchBlockingStub nodeSearch()
    {
        return nodeSearchStub;
    }

    RepositoryAdminGrpc.RepositoryAdminBlockingStub repositoryAdmin()
    {
        return repositoryAdminStub;
    }

    BinariesGrpc.BinariesBlockingStub binaries()
    {
        return binariesBlockingStub;
    }

    BinariesGrpc.BinariesStub binariesAsync()
    {
        return binariesAsyncStub;
    }
}
