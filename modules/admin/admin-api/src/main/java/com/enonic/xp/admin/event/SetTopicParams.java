package com.enonic.xp.admin.event;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.security.PrincipalKeys;

import static java.util.Objects.requireNonNull;

/**
 * Parameters for {@link AdminEventHub#setTopic}.
 */
@NullMarked
public final class SetTopicParams
{
    private final ApplicationKey owner;

    private final String name;

    private final PrincipalKeys allow;

    private SetTopicParams( final Builder builder )
    {
        this.owner = requireNonNull( builder.owner, "owner is required" );
        this.name = requireNonNull( builder.name, "name is required" );
        this.allow = requireNonNull( builder.allow, "allow is required" );
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
     * Builder for {@link SetTopicParams}. All fields are required.
     */
    public static final class Builder
    {
        private @Nullable ApplicationKey owner;

        private @Nullable String name;

        private @Nullable PrincipalKeys allow;

        private Builder()
        {
        }

        /**
         * Sets the owning application.
         *
         * @param owner owning application
         * @return the Builder instance for chaining
         */
        public Builder owner( final ApplicationKey owner )
        {
            this.owner = owner;
            return this;
        }

        /**
         * Sets the local topic name.
         *
         * @param name local topic name: 1-255 characters, no {@code ':'}, no whitespace
         * @return the Builder instance for chaining
         */
        public Builder name( final String name )
        {
            this.name = name;
            return this;
        }

        /**
         * Sets the principals allowed to subscribe.
         *
         * @param allow principals allowed to subscribe, in addition to {@code role:system.admin};
         *              empty clears the topic registration
         * @return the Builder instance for chaining
         */
        public Builder allow( final PrincipalKeys allow )
        {
            this.allow = allow;
            return this;
        }

        /**
         * Builds the SetTopicParams.
         *
         * @return SetTopicParams instance
         */
        public SetTopicParams build()
        {
            return new SetTopicParams( this );
        }
    }
}
