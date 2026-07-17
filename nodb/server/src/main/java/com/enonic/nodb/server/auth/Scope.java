package com.enonic.nodb.server.auth;

/**
 * The two token scopes (DESIGN.md §7.3): {@code runtime} is held by XP and accepted by
 * data-plane RPCs; {@code operator} is held by humans/CI via {@code enonic auth} and is
 * additionally required for management-plane RPCs (repo/branch lifecycle, snapshots,
 * bulk transfer). A runtime credential can never drop a repo.
 */
public enum Scope
{
    RUNTIME,
    OPERATOR;

    /** Case-insensitive parse of the JWT {@code scope} claim; throws on anything else. */
    public static Scope fromClaim( String claim )
    {
        if ( claim == null )
        {
            throw new IllegalArgumentException( "Missing scope claim" );
        }
        return switch ( claim.toLowerCase() )
        {
            case "runtime" -> RUNTIME;
            case "operator" -> OPERATOR;
            default -> throw new IllegalArgumentException( "Unknown scope claim: " + claim );
        };
    }
}
