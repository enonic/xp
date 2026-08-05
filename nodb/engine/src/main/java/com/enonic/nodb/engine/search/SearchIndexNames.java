package com.enonic.nodb.engine.search;

import java.util.regex.Pattern;

import com.enonic.nodb.engine.TenantContext;

/**
 * One-way construction of OpenSearch index and alias names from a {@link TenantContext}
 * plus a repository id (DESIGN.md §5).
 *
 * <p><b>Alias {@code <tenant>-<repo>} over physical {@code <tenant>-<repo>+g<N>}.</b> There
 * is only ONE index kind per repo — Postgres replaced the ES storage index — so there is no
 * {@code search}/{@code storage} discriminator in the name. Queries always target the alias;
 * a rebuild (template change, the V2 layout swap, an ICU or OpenSearch major upgrade) builds
 * {@code +g(N+1)}, catches it up, and flips the alias in one atomic cluster-state update.
 *
 * <p><b>The delimiters are watertight by alphabet, not by convention.</b> Tenant ids are
 * control-plane-minted {@code ^[a-z][a-z0-9]{2,30}$} (enforced by {@link TenantContext}'s own
 * constructor) and therefore contain no {@code -}, so the FIRST dash is always the
 * tenant/repo boundary even though repository ids may contain dashes, even consecutive ones.
 * {@code +} is illegal in a RepositoryId but legal mid-name in OpenSearch, so {@code +g<N>}
 * can never collide with an alias — which matters because aliases and indices share one
 * namespace.
 *
 * <p><b>Nothing here parses a name back.</b> That is deliberate and load-bearing: the
 * authoritative alias→generation mapping is NoDB metadata ({@code search_index}, migration
 * 003), and repo/branch attribution on a search hit rides explicit response fields rather
 * than string-slicing {@code _index}. Today's ES code derives repo by slicing the index name
 * and branch from {@code _type}; under generational names that breaks. There is no
 * {@code parse(...)} method on this class on purpose — adding one would re-open exactly that
 * hole.
 */
public final class SearchIndexNames
{
    /**
     * XP's own RepositoryId grammar. Validated here for the same reason {@link TenantContext}
     * validates tenant ids: an index name is interpolated into a REST path, so this is the
     * injection defense for every OpenSearch call built from a repo id — and it is what makes
     * the "first dash is the boundary" rule true rather than hoped for.
     */
    private static final Pattern VALID_REPO_ID = Pattern.compile( "^[a-z0-9][a-z0-9_.-]*$" );

    /** Generation suffix marker; illegal in a repo id, legal mid-name in OpenSearch. */
    private static final String GENERATION_SEPARATOR = "+g";

    public static final int FIRST_GENERATION = 1;

    private SearchIndexNames()
    {
    }

    /** The alias every query targets: {@code <tenant>-<repo>}. */
    public static String alias( TenantContext tenant, String repoId )
    {
        return tenant.tenantId() + "-" + validRepoId( repoId );
    }

    /** The physical index behind a generation: {@code <tenant>-<repo>+g<N>}. */
    public static String physical( TenantContext tenant, String repoId, int generation )
    {
        return physicalFromAlias( alias( tenant, repoId ), generation );
    }

    /**
     * Same construction, from an alias already built by {@link #alias}. Not a parse: the
     * alias is a value NoDB itself produced and stored, and the generation is read from
     * {@code search_index}, so no information is recovered from the string.
     */
    public static String physicalFromAlias( String alias, int generation )
    {
        if ( generation < FIRST_GENERATION )
        {
            throw new IllegalArgumentException( "Invalid index generation: " + generation );
        }
        return alias + GENERATION_SEPARATOR + generation;
    }

    /** The prefix that scopes every index of one tenant — the dual-tenant isolation boundary. */
    public static String tenantPrefix( TenantContext tenant )
    {
        return tenant.tenantId() + "-";
    }

    /**
     * Whether a name belongs to the cluster's own plumbing rather than to a tenant.
     *
     * <p>OpenSearch 3.7's bundled plugins create system indices in NoDB's cluster the moment
     * the node boots — {@code .plugins-ml-config}, {@code .opendistro-*}, {@code top_queries-*}
     * (the query-insights plugin's rolling indices) — and they are not hypothetical: they show
     * up on a plain single-node dev container with no configuration at all. Any enumeration
     * (readiness, per-tenant listing, Gate G's rebuild drill) must skip them, or "list this
     * cluster's indices" silently includes indices no tenant owns and a "drop everything and
     * rebuild" drill tries to delete the ML plugin's config.
     */
    public static boolean isSystemIndex( String name )
    {
        return name.startsWith( "." ) || name.startsWith( "top_queries-" ) || name.startsWith( "security-auditlog-" );
    }

    private static String validRepoId( String repoId )
    {
        if ( repoId == null || !VALID_REPO_ID.matcher( repoId ).matches() )
        {
            throw new IllegalArgumentException( "Invalid repository id: " + repoId );
        }
        if ( repoId.contains( GENERATION_SEPARATOR ) )
        {
            // Unreachable through VALID_REPO_ID (no '+'), asserted anyway: this is the
            // invariant that makes physical names and aliases non-colliding.
            throw new IllegalArgumentException( "Repository id must not contain '" + GENERATION_SEPARATOR + "': " + repoId );
        }
        return repoId;
    }
}
