package com.enonic.xp.impl.server.rest.api;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.enonic.xp.context.ContextAccessor;

/**
 * Per-API restrictions delivered through the virtual host context of the request being served.
 * <p>
 * A vhost mapping declares them as {@code mapping.<name>.context.api.<application>\:<api>.<setting>}; the values land as
 * context attributes named {@code api.<application>:<api>.<setting>}. The API itself reads and enforces them - there is
 * no central gateway - so an API decides what a setting means. The one setting every management API understands is
 * {@code verbs}: the comma separated list of operations the vhost exposes. An absent setting means no restriction.
 */
public final class ManagementApiPolicy
{
    public static final String PREFIX = "api.";

    public static final String VERBS = "verbs";

    public static final String ANY = "*";

    private final String descriptorKey;

    private final Set<String> verbs;

    private ManagementApiPolicy( final String descriptorKey, final Set<String> verbs )
    {
        this.descriptorKey = descriptorKey;
        this.verbs = verbs;
    }

    /**
     * Resolves the policy for an API from the current context.
     */
    public static ManagementApiPolicy of( final String descriptorKey )
    {
        final Set<String> verbs = setting( descriptorKey, VERBS ).map( ManagementApiPolicy::split ).orElse( null );
        return new ManagementApiPolicy( descriptorKey, verbs );
    }

    /**
     * Reads a single setting of an API from the current context. A vhost mapping delivers strings; an attribute set by
     * other means may be a collection, which is read as its comma separated elements.
     */
    public static Optional<String> setting( final String descriptorKey, final String name )
    {
        final Object value = ContextAccessor.current().getAttribute( PREFIX + descriptorKey + "." + name );
        if ( value instanceof Iterable<?> iterable )
        {
            return Optional.of( StreamSupport.stream( iterable.spliterator(), false ).map( String::valueOf ).collect( Collectors.joining( "," ) ) )
                .filter( s -> !s.isBlank() );
        }
        return Optional.ofNullable( value ).map( String::valueOf ).filter( s -> !s.isBlank() );
    }

    public String getDescriptorKey()
    {
        return descriptorKey;
    }

    /**
     * Returns true when the vhost does not restrict verbs, or names this verb (or {@code *}).
     */
    public boolean allows( final String verb )
    {
        return verbs == null || verbs.contains( ANY ) || verbs.contains( verb );
    }

    private static Set<String> split( final String value )
    {
        return Arrays.stream( value.split( "," ) ).map( String::trim ).filter( s -> !s.isEmpty() ).collect( Collectors.toUnmodifiableSet() );
    }
}
