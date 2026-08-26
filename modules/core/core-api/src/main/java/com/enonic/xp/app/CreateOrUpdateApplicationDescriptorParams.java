package com.enonic.xp.app;

import static java.util.Objects.requireNonNull;

public final class CreateOrUpdateApplicationDescriptorParams
{
    private final ApplicationKey key;

    private final ApplicationDescriptorEditor editor;

    private CreateOrUpdateApplicationDescriptorParams( final Builder builder )
    {
        this.key = builder.key;
        this.editor = builder.editor;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ApplicationKey getKey()
    {
        return key;
    }

    public ApplicationDescriptorEditor getEditor()
    {
        return editor;
    }

    public static final class Builder
    {
        private ApplicationKey key;

        private ApplicationDescriptorEditor editor;

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

        private void validate()
        {
            requireNonNull( key, "key is required" );
            requireNonNull( editor, "editor is required" );
        }

        public CreateOrUpdateApplicationDescriptorParams build()
        {
            validate();
            return new CreateOrUpdateApplicationDescriptorParams( this );
        }
    }
}
