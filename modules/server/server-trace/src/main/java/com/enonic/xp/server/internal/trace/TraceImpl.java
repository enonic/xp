package com.enonic.xp.server.internal.trace;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.TraceLocation;

/**
 * Attributes are stored in a ConcurrentHashMap: trace events are dispatched to listeners on another thread while
 * the traced code may still be adding attributes, so the map must be safe for concurrent reads and iteration.
 * Null values (which ConcurrentHashMap rejects) are treated as removals to keep the lenient Map contract that
 * trace enrichment code relies on.
 * <p>
 * Values are normalized on insertion to the attribute model documented on {@link Trace}
 * (String | Boolean | Long | Double | List&lt;String&gt;), so trace consumers and exporters never observe
 * arbitrary - possibly mutable - objects.
 */
final class TraceImpl
    extends ConcurrentHashMap<String, Object>
    implements Trace
{
    private static final Logger LOG = LoggerFactory.getLogger( TraceImpl.class );

    private static final Set<String> COERCION_LOGGED_TYPES = ConcurrentHashMap.newKeySet();

    private final String id;

    private final String parentId;

    private final String name;

    private Instant startTime;

    private long startTimeNano;

    private Instant endTime;

    private long endTimeNano;

    private final TraceLocation location;

    TraceImpl( final String name, final String parentId, final TraceLocation location )
    {
        this.id = UUID.randomUUID().toString();
        this.parentId = parentId;
        this.name = name;
        this.location = location;
    }

    @Override
    public Object put( final String key, final Object value )
    {
        if ( value == null )
        {
            return remove( key );
        }
        return super.put( key, normalize( value ) );
    }

    @Override
    public void putAll( final Map<? extends String, ?> map )
    {
        map.forEach( this::put );
    }

    private static Object normalize( final Object value )
    {
        if ( value instanceof String || value instanceof Boolean || value instanceof Long || value instanceof Double )
        {
            return value;
        }
        if ( value instanceof Integer || value instanceof Short || value instanceof Byte )
        {
            return ( (Number) value ).longValue();
        }
        if ( value instanceof Float floatValue )
        {
            return floatValue.doubleValue();
        }
        if ( value instanceof Iterable<?> iterable )
        {
            final List<String> strings = new ArrayList<>();
            for ( final Object element : iterable )
            {
                if ( element != null )
                {
                    strings.add( String.valueOf( element ) );
                }
            }
            return List.copyOf( strings );
        }
        if ( COERCION_LOGGED_TYPES.add( value.getClass().getName() ) )
        {
            LOG.debug( "Trace attribute value of type {} converted to String - record strings, booleans, longs, doubles " +
                           "or lists of strings instead", value.getClass().getName() );
        }
        return String.valueOf( value );
    }

    @Override
    public String getId()
    {
        return this.id;
    }

    @Override
    public String getParentId()
    {
        return this.parentId;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public TraceLocation getLocation()
    {
        return this.location;
    }

    @Override
    public Instant getStartTime()
    {
        return this.startTime;
    }

    @Override
    public Instant getEndTime()
    {
        return this.endTime;
    }

    @Override
    public Duration getDuration()
    {
        if ( this.startTime == null )
        {
            return Duration.ZERO;
        }

        if ( this.endTime == null )
        {
            return Duration.ofNanos( System.nanoTime() - this.startTimeNano );
        }

        return Duration.ofNanos( this.endTimeNano - this.startTimeNano );
    }

    @Override
    public boolean inProgress()
    {
        return this.endTime == null;
    }

    @Override
    public void start()
    {
        this.startTime = Instant.now();
        this.startTimeNano = System.nanoTime();
    }

    @Override
    public void end()
    {
        this.endTime = Instant.now();
        this.endTimeNano = System.nanoTime();
    }
}
