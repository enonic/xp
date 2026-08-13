package com.enonic.xp.admin.event;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.security.PrincipalKeys;

import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;

/**
 * Parameters for {@link AdminEventHub#registerTopic}.
 */
@NullMarked
public final class RegisterTopicParams
{
    private final ApplicationKey owner;

    private final String name;

    private final PrincipalKeys allow;

    private RegisterTopicParams( final Builder builder )
    {
        this.owner = requireNonNull( builder.owner, "owner is required" );
        this.name = requireNonNull( builder.name, "name is required" );
        this.allow = requireNonNullElse( builder.allow, PrincipalKeys.empty() );
    }

    /**
     * Returns the owning application.
     */
    public ApplicationKey getOwner()
    {
        return this.owner;
    }

    /**
     * Returns the local topic name.
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Returns the principals allowed to subscribe.
     */
    public PrincipalKeys getAllow()
    {
        return this.allow;
    }

    /**
     * Starts a new builder.
     */
    public static Builder create()
    {
        return new Builder();
    }

    /**
     * Builder for {@link RegisterTopicParams}. {@code owner} and {@code name} are required;
     * {@code allow} defaults to {@link PrincipalKeys#empty()}.
     */
    public static final class Builder
    {
        private @Nullable ApplicationKey owner;

        private @Nullable String name;

        private @Nullable PrincipalKeys allow;

        private Builder()
        {
        }

        public Builder owner( final ApplicationKey owner )
        {
            this.owner = owner;
            return this;
        }

        public Builder name( final String name )
        {
            this.name = name;
            return this;
        }

        public Builder allow( final PrincipalKeys allow )
        {
            this.allow = allow;
            return this;
        }

        public RegisterTopicParams build()
        {
            return new RegisterTopicParams( this );
        }
    }
}
