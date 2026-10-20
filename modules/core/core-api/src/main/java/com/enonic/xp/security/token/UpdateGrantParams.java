package com.enonic.xp.security.token;

import java.util.Set;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleRestriction;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

/**
 * A change to a grant: new scopes, audiences or role restriction. A value left unset keeps what
 * the grant has. The change applies to the grant's access tokens at their next use.
 */
@PublicApi
@NullMarked
public final class UpdateGrantParams
{
    private final PrincipalKey subject;

    private final String grantId;

    private final @Nullable ImmutableSet<String> scope;

    private final @Nullable ImmutableSet<String> audience;

    private final @Nullable RoleRestriction restriction;

    private UpdateGrantParams( final Builder builder )
    {
        this.subject = requireNonNull( builder.subject, "subject is required" );
        checkArgument( this.subject.isUser(), "subject must be a user: %s", this.subject );
        this.grantId = requireNonNull( builder.grantId, "grantId is required" );
        this.scope = builder.scope == null ? null : builder.scope.build();
        this.audience = builder.audience == null ? null : builder.audience.build();
        this.restriction = builder.restriction;
    }

    /**
     * The user the grant belongs to.
     */
    public PrincipalKey getSubject()
    {
        return subject;
    }

    /**
     * The grant to change.
     */
    public String getGrantId()
    {
        return grantId;
    }

    /**
     * The new scopes, or null to keep the grant's.
     */
    public @Nullable Set<String> getScope()
    {
        return scope;
    }

    /**
     * The new audiences, or null to keep the grant's.
     */
    public @Nullable Set<String> getAudience()
    {
        return audience;
    }

    /**
     * The new role restriction, or null to keep the grant's.
     */
    public @Nullable RoleRestriction getRestriction()
    {
        return restriction;
    }

    /**
     * A builder; the subject and grant id are required.
     */
    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private @Nullable PrincipalKey subject;

        private @Nullable String grantId;

        private ImmutableSet.@Nullable Builder<String> scope;

        private ImmutableSet.@Nullable Builder<String> audience;

        private @Nullable RoleRestriction restriction;

        private Builder()
        {
        }

        /**
         * The user the grant belongs to. Required; must be a user key.
         */
        public Builder subject( final PrincipalKey subject )
        {
            this.subject = subject;
            return this;
        }

        /**
         * The grant to change. Required.
         */
        public Builder grantId( final String grantId )
        {
            this.grantId = grantId;
            return this;
        }

        /**
         * The new scopes, replacing the grant's. Call with an empty set for none.
         *
         * @throws IllegalArgumentException if a scope is not a {@link Scopes scope name}
         */
        public Builder scope( final Iterable<String> scope )
        {
            final ImmutableSet.Builder<String> builder = ImmutableSet.builder();
            scope.forEach( s -> builder.add( Scopes.checkScope( s ) ) );
            this.scope = builder;
            return this;
        }

        /**
         * The new audiences, replacing the grant's.
         */
        public Builder audience( final Iterable<String> audience )
        {
            this.audience = ImmutableSet.<String>builder().addAll( audience );
            return this;
        }

        /**
         * The new role restriction, replacing the grant's.
         */
        public Builder restriction( final RoleRestriction restriction )
        {
            this.restriction = restriction;
            return this;
        }

        /**
         * Builds the params.
         *
         * @throws NullPointerException     if the subject or grant id is missing
         * @throws IllegalArgumentException if the subject is not a user
         */
        public UpdateGrantParams build()
        {
            return new UpdateGrantParams( this );
        }
    }
}
