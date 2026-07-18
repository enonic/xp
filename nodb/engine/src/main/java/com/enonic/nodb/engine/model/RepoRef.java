package com.enonic.nodb.engine.model;

/**
 * Repository identity at the engine layer. Tenant is NOT part of this type: the engine
 * always runs within an already-resolved {@code TenantContext} (Tx sets the role and
 * search_path for the whole call), so a repo is addressed by its external id alone here.
 * Stores resolve {@code repoId} to the surrogate {@code repo_key} (see schema.sql's
 * {@code repository} table) via a lookup, never by parsing/deriving it.
 */
public record RepoRef(String repoId)
{
}
