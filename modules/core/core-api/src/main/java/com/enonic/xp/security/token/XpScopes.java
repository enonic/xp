package com.enonic.xp.security.token;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.annotation.PublicApi;

/**
 * The scopes XP defines for its own facilities. Each area has a scope for full access, named after
 * the area, and one for reading, {@code read:<area>}, which the former includes. {@link #READ}
 * includes reading every area, and {@link #ADMIN} everything.
 * <p>
 * XP's management APIs accept them: reading an API takes the read scope of its area, anything else
 * the area's own. An application's API may accept them too.
 */
@PublicApi
@NullMarked
public final class XpScopes
{
    /**
     * Content and projects.
     */
    public static final String CONTENT = "content";

    /**
     * Repositories: their data, indexes, snapshots, dumps and exports.
     */
    public static final String REPO = "repo";

    /**
     * Applications: installing, starting and stopping them.
     */
    public static final String APP = "app";

    /**
     * Id providers and their users.
     */
    public static final String IDPROVIDER = "idprovider";

    /**
     * The system itself: tasks, scheduled jobs, the audit log and maintenance.
     */
    public static final String SYSTEM = "system";

    /**
     * Reading every area.
     */
    public static final String READ = "read";

    /**
     * Everything.
     */
    public static final String ADMIN = "admin";

    private static final List<String> AREAS = List.of( CONTENT, REPO, APP, IDPROVIDER, SYSTEM );

    private static final Scopes ALL = build();

    private XpScopes()
    {
    }

    /**
     * The read scope of an area: {@code read:<area>}.
     */
    public static String read( final String area )
    {
        return READ + ":" + area;
    }

    /**
     * XP's scopes: the widest first, then each area's scope followed by its read scope.
     */
    public static Scopes all()
    {
        return ALL;
    }

    private static Scopes build()
    {
        final List<Scope> scopes = new ArrayList<>();
        final List<String> everything = new ArrayList<>( AREAS );
        everything.add( READ );
        scopes.add( scope( ADMIN, "Full access", "Do everything you can do", everything ) );
        scopes.add( scope( READ, "Read everything", "Read everything you can read", AREAS.stream().map( XpScopes::read ).toList() ) );
        add( scopes, CONTENT, "content", "content and projects" );
        add( scopes, REPO, "repositories", "repositories, indexes, snapshots, dumps and exports" );
        add( scopes, APP, "applications", "applications" );
        add( scopes, IDPROVIDER, "ID providers", "ID providers and their users" );
        add( scopes, SYSTEM, "the system", "tasks, scheduled jobs, the audit log and system maintenance" );
        return Scopes.from( scopes );
    }

    private static void add( final List<Scope> scopes, final String area, final String name, final String what )
    {
        scopes.add( scope( area, "Manage " + name, "Read and change " + what, List.of( read( area ) ) ) );
        scopes.add( scope( read( area ), "Read " + name, "Read " + what, List.of() ) );
    }

    private static Scope scope( final String id, final String title, final String description, final List<String> includes )
    {
        return Scope.create().id( id ).title( title ).description( description ).includes( Set.copyOf( includes ) ).build();
    }
}
