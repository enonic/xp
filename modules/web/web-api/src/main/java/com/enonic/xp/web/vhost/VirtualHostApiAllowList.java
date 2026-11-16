package com.enonic.xp.web.vhost;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;

/**
 * The APIs a virtual host mapping exposes ({@code mapping.<name>.context.api.allow}): a comma separated list of
 * {@code <application>:<api>} entries, where {@code <application>:*} names every API of that application.
 * <p>
 * An API that is not on the list is not reachable through the mapping, even when it is mounted. Read through
 * {@link VirtualHost#getApiAllowList()}.
 */
@NullMarked
public final class VirtualHostApiAllowList
{
    /**
     * The context key holding the list: {@code mapping.<name>.context.api.allow}.
     */
    public static final String CONTEXT_KEY = "api.allow";

    private static final String ANY_API = "*";

    private final Set<DescriptorKey> apis;

    private final Set<ApplicationKey> applications;

    private VirtualHostApiAllowList( final Set<DescriptorKey> apis, final Set<ApplicationKey> applications )
    {
        this.apis = apis;
        this.applications = applications;
    }

    /**
     * Parses a comma separated list of {@code <application>:<api>} and {@code <application>:*} entries.
     *
     * @throws IllegalArgumentException when the list is empty or an entry is not a valid API key
     */
    public static VirtualHostApiAllowList parse( final String value )
    {
        final Set<String> entries =
            Arrays.stream( value.split( "," ) ).map( String::trim ).filter( s -> !s.isEmpty() ).collect( Collectors.toSet() );
        if ( entries.isEmpty() )
        {
            throw new IllegalArgumentException( "API allow list must name at least one API" );
        }

        final Set<ApplicationKey> applications = entries.stream()
            .filter( entry -> entry.endsWith( ":" + ANY_API ) )
            .map( entry -> ApplicationKey.from( entry.substring( 0, entry.length() - ANY_API.length() - 1 ) ) )
            .collect( Collectors.toUnmodifiableSet() );

        final Set<DescriptorKey> apis = entries.stream()
            .filter( entry -> !entry.endsWith( ":" + ANY_API ) )
            .map( DescriptorKey::from )
            .collect( Collectors.toUnmodifiableSet() );

        return new VirtualHostApiAllowList( apis, applications );
    }

    /**
     * Returns the APIs named one by one.
     */
    public Set<DescriptorKey> getApis()
    {
        return apis;
    }

    /**
     * Returns the applications whose every API is allowed ({@code <application>:*} entries).
     */
    public Set<ApplicationKey> getApplications()
    {
        return applications;
    }

    /**
     * Returns true when the list names the API, or its application with {@code *}.
     */
    public boolean allows( final DescriptorKey api )
    {
        return apis.contains( api ) || applications.contains( api.getApplicationKey() );
    }
}
