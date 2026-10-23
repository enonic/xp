package com.enonic.xp.core.impl.app;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.LongSupplier;

import org.jspecify.annotations.Nullable;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.event.ApplicationEvents;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeVersionId;

/**
 * The generation of every application on this cluster node: a counter that moves whenever the definition of the application
 * changes, its bundle, its descriptor or its schema. Values derived from the resources of an application are cached under its
 * generation and are recomputed once it has moved.
 * <p>
 * The generation lives in memory only. It moves on three signals:
 * <ul>
 * <li>synchronously, by the services writing a change on this node, so a read following a write sees it;</li>
 * <li>by the {@link ApplicationEvents#UPDATED} event, for a change written on another node of the cluster;</li>
 * <li>by the version of the application node, which every write of the persisted descriptor or schema moves. Events are not
 * guaranteed to be delivered: the version is read again on use once the check interval has passed, so a change whose event got
 * lost is seen within the interval. Nothing is polled, an application nobody reads is never checked.</li>
 * </ul>
 * A node starting up starts from zero, together with the empty caches the generation guards.
 */
@Component(immediate = true, service = {ApplicationGeneration.class, EventListener.class}, configurationPid = "com.enonic.xp.schema")
public final class ApplicationGeneration
    implements EventListener
{
    private static final Logger LOG = LoggerFactory.getLogger( ApplicationGeneration.class );

    private final ConcurrentMap<ApplicationKey, AtomicReference<State>> states = new ConcurrentHashMap<>();

    private final Function<ApplicationKey, @Nullable NodeVersionId> versionReader;

    private final LongSupplier nanoClock;

    private volatile long checkIntervalNanos;

    @Activate
    public ApplicationGeneration( @Reference final NodeService nodeService, final SchemaConfig config )
    {
        this( applicationKey -> readVersion( nodeService, applicationKey ), System::nanoTime, config.cache_checkInterval() );
    }

    /**
     * Checks the application node at most once per second.
     */
    public ApplicationGeneration( final NodeService nodeService )
    {
        this( applicationKey -> readVersion( nodeService, applicationKey ), System::nanoTime, SchemaConfig.DEFAULT_CACHE_CHECK_INTERVAL );
    }

    ApplicationGeneration( final Function<ApplicationKey, @Nullable NodeVersionId> versionReader, final LongSupplier nanoClock,
                           final long checkIntervalMillis )
    {
        this.versionReader = versionReader;
        this.nanoClock = nanoClock;
        this.checkIntervalNanos = toNanos( checkIntervalMillis );
    }

    @Modified
    public void modified( final SchemaConfig config )
    {
        this.checkIntervalNanos = toNanos( config.cache_checkInterval() );
    }

    private static long toNanos( final long checkIntervalMillis )
    {
        return Duration.ofMillis( Math.max( 0, checkIntervalMillis ) ).toNanos();
    }

    /**
     * The current generation of the application. The version of the application node is read first when the last check is older
     * than the interval: by one of the callers, the others meanwhile go on with the generation as it is.
     */
    public long of( final ApplicationKey applicationKey )
    {
        final AtomicReference<State> ref = state( applicationKey );
        final State state = ref.get();
        final long now = nanoClock.getAsLong();
        if ( now - state.checkedAt() < checkIntervalNanos )
        {
            return state.generation();
        }
        if ( ref.compareAndSet( state, state.withCheckedAt( now ) ) )
        {
            check( applicationKey, ref );
        }
        return ref.get().generation();
    }

    /**
     * The current generation of the application, the application node not read: for an application none of whose resources are
     * served from nodes, a local one, which no write of the persisted descriptor or schema can make stale.
     */
    public long current( final ApplicationKey applicationKey )
    {
        return state( applicationKey ).get().generation();
    }

    /**
     * Moves the generation of the application: everything cached under the previous one is stale from now on.
     */
    public void bump( final ApplicationKey applicationKey )
    {
        state( applicationKey ).updateAndGet( State::next );
    }

    /**
     * A change written on another node of the cluster: told by the distributed {@link ApplicationEvents#UPDATED} event. Local changes
     * bump the generation where they are written, before the event is published.
     */
    @Override
    public void onEvent( final Event event )
    {
        if ( event.isLocalOrigin() || !ApplicationEvents.EVENT_TYPE.equals( event.getType() ) )
        {
            return;
        }
        if ( !event.getValueAs( String.class, ApplicationEvents.EVENT_TYPE_KEY ).filter( ApplicationEvents.UPDATED::equals ).isPresent() )
        {
            return;
        }
        event.getValueAs( String.class, ApplicationEvents.APPLICATION_KEY_KEY ).map( ApplicationKey::from ).ifPresent( this::bump );
    }

    private AtomicReference<State> state( final ApplicationKey applicationKey )
    {
        // a new state is due for a check right away
        return states.computeIfAbsent( applicationKey,
                                       key -> new AtomicReference<>( State.initial( nanoClock.getAsLong() - checkIntervalNanos ) ) );
    }

    private void check( final ApplicationKey applicationKey, final AtomicReference<State> ref )
    {
        final NodeVersionId version;
        try
        {
            version = versionReader.apply( applicationKey );
        }
        catch ( Exception e )
        {
            // the repository unavailable (not yet initialized, for instance): what is cached is kept, checked again after the interval
            LOG.debug( "Cannot check the version of application [{}]", applicationKey, e );
            return;
        }
        // the first version read moves the generation too: a value computed before it is known may predate it
        ref.updateAndGet(
            state -> state.known() && Objects.equals( state.version(), version ) ? state : state.next().withVersion( version ) );
    }

    private static @Nullable NodeVersionId readVersion( final NodeService nodeService, final ApplicationKey applicationKey )
    {
        final Node node =
            ApplicationHelper.runAsAdmin( () -> nodeService.getByPath( ApplicationRepoServiceImpl.applicationNodePath( applicationKey ) ) );
        return node == null ? null : node.getNodeVersionId();
    }

    /**
     * @param version the version of the application node last read, {@code null} when there is no node
     * @param known   whether the version was read at all
     */
    private record State(long generation, @Nullable NodeVersionId version, boolean known, long checkedAt)
    {
        static State initial( final long checkedAt )
        {
            return new State( 0, null, false, checkedAt );
        }

        State next()
        {
            return new State( generation + 1, version, known, checkedAt );
        }

        State withVersion( final @Nullable NodeVersionId version )
        {
            return new State( generation, version, true, checkedAt );
        }

        State withCheckedAt( final long checkedAt )
        {
            return new State( generation, version, known, checkedAt );
        }
    }
}
