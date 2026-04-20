package com.enonic.xp.task;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.descriptor.DescriptorKey;

import static java.util.Objects.requireNonNull;


public final class SubmitTaskParams
{
    private final DescriptorKey descriptorKey;

    private final String name;

    private final PropertyTree data;

    private SubmitTaskParams( final Builder builder )
    {
        this.descriptorKey = requireNonNull( builder.descriptorKey, "descriptor key is required" );
        this.name = builder.name;
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

    public String getName()
    {
        return name;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private DescriptorKey descriptorKey;

        private PropertyTree data;

        private String name;

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

        public Builder name( final String name )
        {
            this.name = name;
            return this;
        }

        public SubmitTaskParams build()
        {
            return new SubmitTaskParams( this );
        }
    }
}
