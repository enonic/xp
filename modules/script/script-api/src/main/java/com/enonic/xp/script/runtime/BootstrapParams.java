package com.enonic.xp.script.runtime;

import java.util.Objects;
import java.util.Optional;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.ResourceKey;

/**
 * Parameters for {@link ScriptRuntime#bootstrap(BootstrapParams)}. Calling {@code bootstrap} arms
 * the application's gate that top-level executions wait on; running a bootstrap script is optional —
 * {@code mainScript} may be absent, in which case the gate simply opens.
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

    public ApplicationKey getApplication()
    {
        return this.application;
    }

    public Optional<ResourceKey> getMainScript()
    {
        return Optional.ofNullable( this.mainScript );
    }

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

        public Builder application( final ApplicationKey application )
        {
            this.application = application;
            return this;
        }

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
