package com.enonic.xp.security.token;

import java.time.Duration;
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
 * What a user approved; see {@link Grant} for each field. The caller has already checked the
 * approval against what may be granted.
 */
@PublicApi
@NullMarked
public final class CreateGrantParams
{
    private final PrincipalKey subject;

    private final String clientId;

    private final ImmutableSet<String> audience;

    private final ImmutableSet<String> scope;

    private final RoleRestriction restriction;

    private final @Nullable Duration ttl;

    private CreateGrantParams( final Builder builder )
    {
        this.subject = requireNonNull( builder.subject, "subject is required" );
        checkArgument( this.subject.isUser(), "subject must be a user: %s", this.subject );
        this.clientId = requireNonNull( builder.clientId, "clientId is required" );
        checkArgument( !this.clientId.isBlank(), "clientId must not be blank" );
        this.audience = builder.audience.build();
        this.scope = builder.scope.build();
        this.restriction = builder.restriction;
        this.ttl = builder.ttl;
        checkArgument( ttl == null || ( !ttl.isNegative() && !ttl.isZero() ), "ttl must be positive: %s", ttl );
    }

    /**
     * The user who approved the grant.
     */
    public PrincipalKey getSubject()
    {
        return subject;
    }

    /**
     * The client the grant is for, as the client named itself. Any value is accepted: XP has no
     * client registry yet.
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
     * The role restriction approved.
     */
    public RoleRestriction getRestriction()
    {
        return restriction;
    }

    /**
     * How long the grant lives, or null for the configured default.
     */
    public @Nullable Duration getTtl()
    {
        return ttl;
    }

    /**
     * A builder; the subject is required.
     */
    public static Builder create()
    {
        return new Builder();
    }

    /**
     * Builds the params. Audiences and scopes accumulate across calls; other values replace.
     */
    public static final class Builder
    {
        private @Nullable PrincipalKey subject;

        private @Nullable String clientId;

        private final ImmutableSet.Builder<String> audience = ImmutableSet.builder();

        private final ImmutableSet.Builder<String> scope = ImmutableSet.builder();

        private RoleRestriction restriction = RoleRestriction.none();

        private @Nullable Duration ttl;

        private Builder()
        {
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
         * The client the grant is for, as it identified itself. Required and not blank.
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
         * Adds approved audience names.
         */
        public Builder audience( final String... audience )
        {
            this.audience.add( audience );
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
         * Adds approved scopes.
         *
         * @throws IllegalArgumentException when a scope is not a {@link Scopes scope name}
         */
        public Builder scope( final String... scope )
        {
            for ( String s : scope )
            {
                this.scope.add( Scopes.checkScope( s ) );
            }
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
         * How long the grant lives; null (the default) for the configured default.
         */
        public Builder ttl( final @Nullable Duration ttl )
        {
            this.ttl = ttl;
            return this;
        }

        /**
         * Builds the params.
         * 
         * @throws NullPointerException     when the subject is missing
         * @throws IllegalArgumentException when the subject is not a user or the ttl is not positive
         */
        public CreateGrantParams build()
        {
            return new CreateGrantParams( this );
        }
    }
}
