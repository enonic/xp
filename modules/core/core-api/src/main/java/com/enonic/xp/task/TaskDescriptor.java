package com.enonic.xp.task;

import com.google.common.base.Preconditions;

import com.enonic.xp.form.Form;
import com.enonic.xp.page.DescriptorKey;

public class TaskDescriptor
{
    private final DescriptorKey key;

    private final String description;

    private final Form form;

    private TaskDescriptor( final Builder builder )
    {
        Preconditions.checkNotNull( builder.key, "key cannot be null" );
        this.key = builder.key;
        this.description = builder.description;
        this.form = builder.form;
    }

    public DescriptorKey getKey()
    {
        return key;
    }

    public String getDescription()
    {
        return description;
    }

    public Form getForm()
    {
        return form;
    }

    private static class Builder
    {
        private DescriptorKey key;

        private String description;

        private Form form;

        private Builder()
        {
        }

        public Builder setKey( final DescriptorKey key )
        {
            this.key = key;
            return this;
        }

        public Builder setDescription( final String description )
        {
            this.description = description;
            return this;
        }

        public Builder setForm( final Form form )
        {
            this.form = form;
            return this;
        }

        public TaskDescriptor build()
        {
            return new TaskDescriptor( this );
        }
    }
}
