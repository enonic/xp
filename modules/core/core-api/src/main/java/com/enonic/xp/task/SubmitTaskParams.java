package com.enonic.xp.task;

import java.util.Objects;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.page.DescriptorKey;

public final class SubmitTaskParams
{
    private final DescriptorKey descriptorKey;

    private final PropertyTree config;

    private final boolean offload;

    private SubmitTaskParams( final Builder builder )
    {
        this.descriptorKey = Objects.requireNonNull( builder.descriptorKey, "descriptor key is required" );
        this.config = builder.config;
        this.offload = builder.offload;
    }

    public DescriptorKey getDescriptorKey()
    {
        return descriptorKey;
    }

    public PropertyTree getConfig()
    {
        return config;
    }

    public boolean isOffload()
    {
        return offload;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private DescriptorKey descriptorKey;

        private PropertyTree config;

        private boolean offload;

        private Builder()
        {
        }

        public Builder descriptorKey( final DescriptorKey descriptorKey )
        {
            this.descriptorKey = descriptorKey;
            return this;
        }

        public Builder config( final PropertyTree config )
        {
            this.config = config;
            return this;
        }

        public Builder offload( final boolean offload )
        {
            this.offload = offload;
            return this;
        }

        public SubmitTaskParams build()
        {
            return new SubmitTaskParams( this );
        }
    }
}
