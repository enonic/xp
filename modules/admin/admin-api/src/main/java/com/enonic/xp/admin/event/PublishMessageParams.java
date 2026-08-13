package com.enonic.xp.admin.event;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.util.GenericValue;

import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;

/**
 * Parameters for {@link AdminEventHub#publish}.
 */
@NullMarked
public final class PublishMessageParams
{
    private static final GenericValue EMPTY_MESSAGE = GenericValue.newObject().build();

    private final ApplicationKey caller;

    private final String name;

    private final GenericValue message;

    private PublishMessageParams( final Builder builder )
    {
        this.caller = requireNonNull( builder.caller, "caller is required" );
        this.name = requireNonNull( builder.name, "name is required" );
        this.message = requireNonNullElse( builder.message, EMPTY_MESSAGE );
    }

    /**
     * Returns the publishing application.
     */
    public ApplicationKey getCaller()
    {
        return this.caller;
    }

    /**
     * Returns the local topic name, as passed to {@link AdminEventHub#registerTopic}.
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Returns the message data.
     */
    public GenericValue getMessage()
    {
        return this.message;
    }

    /**
     * Starts a new builder.
     */
    public static Builder create()
    {
        return new Builder();
    }

    /**
     * Builder for {@link PublishMessageParams}. {@code caller} and {@code name} are required;
     * {@code message} defaults to an empty object.
     */
    public static final class Builder
    {
        private @Nullable ApplicationKey caller;

        private @Nullable String name;

        private @Nullable GenericValue message;

        private Builder()
        {
        }

        public Builder caller( final ApplicationKey caller )
        {
            this.caller = caller;
            return this;
        }

        public Builder name( final String name )
        {
            this.name = name;
            return this;
        }

        public Builder message( final GenericValue message )
        {
            this.message = message;
            return this;
        }

        public PublishMessageParams build()
        {
            return new PublishMessageParams( this );
        }
    }
}
