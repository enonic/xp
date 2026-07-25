package com.enonic.xp.script.runtime;

import java.util.Objects;
import java.util.Optional;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.ResourceKey;

/**
 * Parameters for {@link ScriptRuntime#bootstrap(BootstrapParams)}. Every application must be
 * bootstrapped before its other scripts execute. Running a bootstrap script is optional:
 * {@code mainScript} may be absent, in which case the application simply becomes ready.
 */
@NullMarked
public final class BootstrapParams
{
    private final ApplicationKey application;

    private final @Nullable ResourceKey mainScript;

    private BootstrapParams( final Builder builder )
    {
        this.application = Objects.requireNonNull( builder.application, "application is required" );
        if ( builder.mainScript != null && !this.application.equals( builder.mainScript.getApplicationKey() ) )
        {
            throw new IllegalArgumentException(
                "mainScript [" + builder.mainScript + "] does not belong to application [" + this.application + "]" );
        }
        this.mainScript = builder.mainScript;
    }

    /**
     * The application to bootstrap.
     */
    public ApplicationKey getApplication()
    {
        return this.application;
    }

    /**
     * The bootstrap script to run, when the application has one.
     */
    public Optional<ResourceKey> getMainScript()
    {
        return Optional.ofNullable( this.mainScript );
    }

    /**
     * Creates a new builder.
     */
    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private @Nullable ApplicationKey application;

        private @Nullable ResourceKey mainScript;

        private Builder()
        {
        }

        /**
         * The application to bootstrap. Required.
         */
        public Builder application( final ApplicationKey application )
        {
            this.application = application;
            return this;
        }

        /**
         * The bootstrap script to run. Optional: without it, the application becomes ready
         * without executing a script.
         */
        public Builder mainScript( final @Nullable ResourceKey mainScript )
        {
            this.mainScript = mainScript;
            return this;
        }

        /**
         * @throws NullPointerException when {@code application} is not set
         * @throws IllegalArgumentException when {@code mainScript} belongs to a different
         * application than {@code application}
         */
        public BootstrapParams build()
        {
            return new BootstrapParams( this );
        }
    }
}
