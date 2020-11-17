package com.enonic.xp.task;

import java.util.Objects;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.page.DescriptorKey;

public final class SubmitTaskParams
{
    private final DescriptorKey descriptorKey;

    private final PropertyTree data;

    private SubmitTaskParams( final Builder builder )
    {
        this.descriptorKey = Objects.requireNonNull( builder.descriptorKey, "descriptor key is required" );
        this.data = builder.data;
    }

    public DescriptorKey getDescriptorKey()
    {
        return descriptorKey;
    }

    public PropertyTree getData()
    {
        return data;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private DescriptorKey descriptorKey;

        private PropertyTree data;

        private Builder()
        {
        }

        public Builder descriptorKey( final DescriptorKey descriptorKey )
        {
            this.descriptorKey = descriptorKey;
            return this;
        }

        public Builder data( final PropertyTree data )
        {
            this.data = data;
            return this;
        }

        public SubmitTaskParams build()
        {
            return new SubmitTaskParams( this );
        }
    }
}
