package com.enonic.xp.security.token;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalRestriction;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

/**
 * A user's standing permission for a client to obtain access tokens on their behalf: what was
 * approved (audience, scope, principal restriction), for which client, and until when. A grant
 * belongs to the user, who sees and revokes it; the access tokens issued under it are not kept.
 * The refresh token that redeems the grant is not part of it.
 */
@PublicApi
@NullMarked
public final class Grant
{
    private final String id;

    private final PrincipalKey subject;

    private final String sid;

    private final @Nullable String clientId;

    private final ImmutableSet<String> audience;

    private final ImmutableSet<String> scope;

    private final PrincipalRestriction restriction;

    private final Instant createdAt;

    private final Instant rotatedAt;

    private final Instant expiresAt;

    private Grant( final Builder builder )
    {
        this.id = requireNonNull( builder.id, "id is required" );
        this.subject = requireNonNull( builder.subject, "subject is required" );
        checkArgument( this.subject.isUser(), "subject must be a user: %s", this.subject );
        this.sid = requireNonNull( builder.sid, "sid is required" );
        this.clientId = builder.clientId;
        this.audience = builder.audience.build();
        this.scope = builder.scope.build();
        this.restriction = builder.restriction;
        this.createdAt = requireNonNull( builder.createdAt, "createdAt is required" );
        this.rotatedAt = requireNonNull( builder.rotatedAt, "rotatedAt is required" );
        this.expiresAt = requireNonNull( builder.expiresAt, "expiresAt is required" );
    }

    /**
     * The grant's id, stable across refresh-token rotations. Access tokens issued under the grant
     * carry it as their grant id.
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
     * The user's sid the grant belongs to, which every access token issued under the grant
     * carries. See {@link AccessTokenClaims#getSid()}.
     */
    public String getSid()
    {
        return sid;
    }

    /**
     * The client the grant is for, or null when unknown.
     */
    public @Nullable String getClientId()
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
     * The principal restriction approved for the grant, which every access token issued under it carries.
     * See {@link AccessTokenClaims#getRestriction()}.
     */
    public PrincipalRestriction getRestriction()
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
     * When the current refresh token was issued: creation counts as the first rotation, and
     * every rotation after it moves the mark. A redeem that returns the token unchanged does
     * not.
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
     * A builder; the id, subject, sid and timestamps are required.
     */
    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public boolean equals( final Object o )
    {
        return this == o || o instanceof Grant that && id.equals( that.id ) && subject.equals( that.subject ) && sid.equals( that.sid ) &&
            Objects.equals( clientId, that.clientId ) && audience.equals( that.audience ) && scope.equals( that.scope ) &&
            restriction.equals( that.restriction ) && createdAt.equals( that.createdAt ) && rotatedAt.equals( that.rotatedAt ) &&
            expiresAt.equals( that.expiresAt );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( id, subject, sid, clientId, audience, scope, restriction, createdAt, rotatedAt, expiresAt );
    }

    @Override
    public String toString()
    {
        return "Grant{" + "id=" + id + ", subject=" + subject + ", sid=" + sid + ", clientId=" + clientId + ", audience=" + audience + ", scope=" + scope +
            ", restriction=" + restriction + ", createdAt=" + createdAt + ", rotatedAt=" + rotatedAt + ", expiresAt=" + expiresAt + '}';
    }

    /**
     * Assembles a grant, normally by the grant service from its store. Audience names and scopes
     * accumulate across calls; the other values replace.
     */
    public static final class Builder
    {
        private @Nullable String id;

        private @Nullable PrincipalKey subject;

        private @Nullable String sid;

        private @Nullable String clientId;

        private final ImmutableSet.Builder<String> audience = ImmutableSet.builder();

        private final ImmutableSet.Builder<String> scope = ImmutableSet.builder();

        private PrincipalRestriction restriction = PrincipalRestriction.none();

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
         * The user's sid the grant belongs to. Required.
         */
        public Builder sid( final String sid )
        {
            this.sid = sid;
            return this;
        }

        /**
         * The client the grant is for; null (the default) when unknown.
         */
        public Builder clientId( final @Nullable String clientId )
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
         * @throws IllegalArgumentException when a scope is not a {@link Scopes scope name}
         */
        public Builder scope( final Iterable<String> scope )
        {
            scope.forEach( s -> this.scope.add( Scopes.checkScope( s ) ) );
            return this;
        }

        /**
         * The principal restriction approved; none by default.
         */
        public Builder restriction( final PrincipalRestriction restriction )
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
         * When the current refresh token was issued, creation counting as the first rotation.
         * Required.
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
