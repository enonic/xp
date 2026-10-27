package com.enonic.xp.security.token;

import java.time.Instant;
import java.util.Objects;
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
 * A user's standing permission for a client to obtain access tokens on their behalf: what was
 * approved, for which client, until when. The user sees and revokes it. Access tokens issued under
 * it are not kept: they are accepted while it exists and run with what it holds now. The refresh
 * token is not part of it.
 */
@PublicApi
@NullMarked
public final class Grant
{
    private final String id;

    private final PrincipalKey subject;

    private final String clientId;

    private final ImmutableSet<String> audience;

    private final ImmutableSet<String> scope;

    private final RoleRestriction restriction;

    private final Instant createdAt;

    private final Instant rotatedAt;

    private final Instant expiresAt;

    private Grant( final Builder builder )
    {
        this.id = requireNonNull( builder.id, "id is required" );
        this.subject = requireNonNull( builder.subject, "subject is required" );
        checkArgument( this.subject.isUser(), "subject must be a user: %s", this.subject );
        this.clientId = requireNonNull( builder.clientId, "clientId is required" );
        this.audience = builder.audience.build();
        this.scope = builder.scope.build();
        this.restriction = builder.restriction;
        this.createdAt = requireNonNull( builder.createdAt, "createdAt is required" );
        this.rotatedAt = requireNonNull( builder.rotatedAt, "rotatedAt is required" );
        this.expiresAt = requireNonNull( builder.expiresAt, "expiresAt is required" );
    }

    /**
     * The grant's id, stable across refresh-token rotations. Access tokens issued under the grant
     * carry it.
     */
    public String getId()
    {
        return id;
    }

    /**
     * The user who approved the grant.
     */
    public PrincipalKey getSubject()
    {
        return subject;
    }

    /**
     * The client the grant is for, as it named itself.
     */
    public String getClientId()
    {
        return clientId;
    }

    /**
     * The audience names approved; empty for none.
     */
    public Set<String> getAudience()
    {
        return audience;
    }

    /**
     * The scopes approved; empty for none.
     */
    public Set<String> getScope()
    {
        return scope;
    }

    /**
     * The role restriction approved; the grant's access tokens run with it.
     */
    public RoleRestriction getRestriction()
    {
        return restriction;
    }

    /**
     * When the grant was created.
     */
    public Instant getCreatedAt()
    {
        return createdAt;
    }

    /**
     * When the current refresh token was issued. Creation counts as the first rotation.
     */
    public Instant getRotatedAt()
    {
        return rotatedAt;
    }

    /**
     * When the grant expires; it cannot be redeemed after this.
     */
    public Instant getExpiresAt()
    {
        return expiresAt;
    }

    /**
     * A builder; the id, subject and timestamps are required.
     */
    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public boolean equals( final Object o )
    {
        return this == o || o instanceof Grant that && id.equals( that.id ) && subject.equals( that.subject ) &&
            clientId.equals( that.clientId ) && audience.equals( that.audience ) && scope.equals( that.scope ) &&
            restriction.equals( that.restriction ) && createdAt.equals( that.createdAt ) && rotatedAt.equals( that.rotatedAt ) &&
            expiresAt.equals( that.expiresAt );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( id, subject, clientId, audience, scope, restriction, createdAt, rotatedAt, expiresAt );
    }

    @Override
    public String toString()
    {
        return "Grant{" + "id=" + id + ", subject=" + subject + ", clientId=" + clientId + ", audience=" + audience + ", scope=" + scope +
            ", restriction=" + restriction + ", createdAt=" + createdAt + ", rotatedAt=" + rotatedAt + ", expiresAt=" + expiresAt + '}';
    }

    /**
     * Builds a grant. Audiences and scopes accumulate across calls; other values replace.
     */
    public static final class Builder
    {
        private @Nullable String id;

        private @Nullable PrincipalKey subject;

        private @Nullable String clientId;

        private final ImmutableSet.Builder<String> audience = ImmutableSet.builder();

        private final ImmutableSet.Builder<String> scope = ImmutableSet.builder();

        private RoleRestriction restriction = RoleRestriction.none();

        private @Nullable Instant createdAt;

        private @Nullable Instant rotatedAt;

        private @Nullable Instant expiresAt;

        private Builder()
        {
        }

        /**
         * The grant's id. Required.
         */
        public Builder id( final String id )
        {
            this.id = id;
            return this;
        }

        /**
         * The user who approved the grant. Required, and must be a user key.
         */
        public Builder subject( final PrincipalKey subject )
        {
            this.subject = subject;
            return this;
        }

        /**
         * The client the grant is for. Required.
         */
        public Builder clientId( final String clientId )
        {
            this.clientId = clientId;
            return this;
        }

        /**
         * Adds approved audience names.
         */
        public Builder audience( final Iterable<String> audience )
        {
            this.audience.addAll( audience );
            return this;
        }

        /**
         * Adds approved scopes.
         *
         * @throws IllegalArgumentException when a scope is not a {@link ScopeNames scope name}
         */
        public Builder scope( final Iterable<String> scope )
        {
            scope.forEach( s -> this.scope.add( ScopeNames.checkScope( s ) ) );
            return this;
        }

        /**
         * The role restriction approved; none by default.
         */
        public Builder restriction( final RoleRestriction restriction )
        {
            this.restriction = restriction;
            return this;
        }

        /**
         * When the grant was created. Required.
         */
        public Builder createdAt( final Instant createdAt )
        {
            this.createdAt = createdAt;
            return this;
        }

        /**
         * When the current refresh token was issued. Required.
         */
        public Builder rotatedAt( final Instant rotatedAt )
        {
            this.rotatedAt = rotatedAt;
            return this;
        }

        /**
         * When the grant expires. Required.
         */
        public Builder expiresAt( final Instant expiresAt )
        {
            this.expiresAt = expiresAt;
            return this;
        }

        /**
         * Builds the grant.
         * 
         * @throws NullPointerException     when a required value is missing
         * @throws IllegalArgumentException when the subject is not a user
         */
        public Grant build()
        {
            return new Grant( this );
        }
    }
}
