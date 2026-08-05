package com.enonic.xp.core.nodb.corpus;

/**
 * Which {@code SearchSource} (and therefore which principal set) a corpus query is issued with.
 * The envelope carries {@code sources[{repo_id, branch, principals[]}]} (Gate 0(b)), so the
 * corpus has to exercise more than one shape of that list -- including the two ACL behaviours
 * the inventory flagged as needing precise porting.
 */
enum SourceKind
{
    /** One source, principals = the ordinary test user (+ role:authenticated, role:everyone). */
    DEFAULT_USER,

    /**
     * One source, principals = {@code role:system.admin}. Today {@code AclFilterBuilderFactory}
     * applies NO filter at all for this set; DESIGN §7.2 replaces that with an injected read-key.
     * This is the "admin sees everything ES-admin saw" query the inventory demands.
     */
    ADMIN,

    /**
     * One source with an EMPTY principal set. Must stay fail-closed (resolve to
     * {@code user:system:anonymous}), never match-all.
     */
    EMPTY_PRINCIPALS,

    /** One source, principals = the corpus "secret" user, who can read only the secret node. */
    SECRET_USER,

    /**
     * Two sources over two extra repositories with PER-SOURCE principal sets -- the
     * multiRepoConnect / cross-project shape (today's MultiRepoSearchSource/RepoBranchAclMap).
     */
    MULTI_REPO_BOTH_ALLOWED,

    /**
     * Two sources where the second source's principals cannot read anything in that repository --
     * pins the per-index ACL fan-out, not a global filter.
     */
    MULTI_REPO_ONE_DENIED
}
