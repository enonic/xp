package com.enonic.xp.task;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

import com.enonic.xp.descriptor.Descriptor;
import com.enonic.xp.form.Form;
import com.enonic.xp.page.DescriptorKey;

@Beta
public final class TaskDescriptor
    extends Descriptor
{
    private final String description;

    private final Form config;

    private TaskDescriptor( final Builder builder )
    {
        super( builder.key );
        this.description = builder.description;
        this.config = builder.config == null ? Form.create().build() : builder.config;
    }

    public String getDescription()
    {
        return description;
    }

    public Form getConfig()
    {
        return config;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public final static class Builder
    {
        private DescriptorKey key;

        private String description;

        private Form config;

        private Builder()
        {
        }

        public Builder key( final DescriptorKey key )
        {
            this.key = key;
            return this;
        }

        public Builder description( final String description )
        {
            this.description = description;
            return this;
        }

        public Builder config( final Form config )
        {
            this.config = config;
            return this;
        }

        public TaskDescriptor build()
        {
            Preconditions.checkNotNull( this.key, "key cannot be null" );
            return new TaskDescriptor( this );
        }
    }
}
