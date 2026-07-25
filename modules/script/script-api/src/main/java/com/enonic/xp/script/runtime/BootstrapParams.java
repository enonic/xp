package com.enonic.xp.script.runtime;

import java.util.Objects;
import java.util.Optional;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.ResourceKey;

/**
 * Parameters for {@link ScriptRuntime#bootstrap(BootstrapParams)}. Every application must be
 * bootstrapped before its other scripts execute. Running a bootstrap script is optional:
 * {@code mainScript} may be absent, in which case the application simply becomes ready.
 */
public final class BootstrapParams
{
    private final ApplicationKey application;

    private final ResourceKey mainScript;

    private BootstrapParams( final Builder builder )
    {
        this.application = Objects.requireNonNull( builder.application, "application is required" );
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
        private ApplicationKey application;

        private ResourceKey mainScript;

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
        public Builder mainScript( final ResourceKey mainScript )
        {
            this.mainScript = mainScript;
            return this;
        }

        public BootstrapParams build()
        {
            return new BootstrapParams( this );
        }
    }
}
