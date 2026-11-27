package com.enonic.xp.web.vhost;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.util.NameFilter;

/**
 * The APIs a virtual host mapping exposes ({@code mapping.<name>.context.api.filter}), matched on their
 * {@code <application>:<api>} keys with the application filter syntax of {@link NameFilter}: a comma separated list of
 * patterns, a trailing {@code *} matching any key with that prefix, a leading {@code !} rejecting, the first matching
 * pattern deciding and an unmatched key rejected. For example {@code media:*, com.example.app:*} or
 * {@code !com.example.app:internal, *}.
 * <p>
 * An API the filter rejects is treated as not existing on the mapping, even when it is mounted. Read through
 * {@link VirtualHost#getApiFilter()}.
 */
@NullMarked
public final class VirtualHostApiFilter
{
    /**
     * The context key holding the filter: {@code mapping.<name>.context.api.filter}.
     */
    public static final String CONTEXT_KEY = "api.filter";

    private final NameFilter filter;

    private VirtualHostApiFilter( final NameFilter filter )
    {
        this.filter = filter;
    }

    /**
     * Parses a comma separated list of API key patterns. A pattern without {@code *} must be a valid
     * {@code <application>:<api>} key.
     *
     * @throws IllegalArgumentException when the list has no patterns or a pattern without {@code *} is not a valid API key
     */
    public static VirtualHostApiFilter parse( final String value )
    {
        final NameFilter filter = NameFilter.parse( value );
        if ( filter.isEmpty() )
        {
            throw new IllegalArgumentException( "API filter must have at least one pattern" );
        }
        filter.getPatterns().stream().filter( pattern -> !pattern.endsWith( "*" ) ).forEach( DescriptorKey::from );
        return new VirtualHostApiFilter( filter );
    }

    /**
     * Returns true when the filter accepts the API.
     */
    public boolean accept( final DescriptorKey api )
    {
        return filter.accept( api.toString() );
    }
}
