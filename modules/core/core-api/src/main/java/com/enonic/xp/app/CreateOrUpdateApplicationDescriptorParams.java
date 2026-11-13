package com.enonic.xp.app;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

public final class CreateOrUpdateApplicationDescriptorParams
{
    private final ApplicationKey key;

    private final ApplicationDescriptorEditor editor;

    private final Boolean locked;

    private CreateOrUpdateApplicationDescriptorParams( final Builder builder )
    {
        this.key = builder.key;
        this.editor = builder.editor;
        this.locked = builder.locked;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ApplicationKey getKey()
    {
        return key;
    }

    /**
     * @return the editor of the descriptor, {@code null} when the descriptor is left as is
     */
    public ApplicationDescriptorEditor getEditor()
    {
        return editor;
    }

    /**
     * Whether installing over and uninstalling the application are to be prevented, see
     * {@link Application#isLocked()}. The flag is kept on the node of the application, never in its descriptor.
     *
     * @return the flag to set, {@code null} when it is left as is
     */
    public Boolean getLocked()
    {
        return locked;
    }

    public static final class Builder
    {
        private ApplicationKey key;

        private ApplicationDescriptorEditor editor;

        private Boolean locked;

        private Builder()
        {
        }

        public Builder key( final ApplicationKey key )
        {
            this.key = key;
            return this;
        }

        public Builder editor( final ApplicationDescriptorEditor editor )
        {
            this.editor = editor;
            return this;
        }

        public Builder locked( final Boolean locked )
        {
            this.locked = locked;
            return this;
        }

        private void validate()
        {
            requireNonNull( key, "key is required" );
            checkArgument( editor != null || locked != null, "editor or locked is required" );
        }

        public CreateOrUpdateApplicationDescriptorParams build()
        {
            validate();
            return new CreateOrUpdateApplicationDescriptorParams( this );
        }
    }
}
