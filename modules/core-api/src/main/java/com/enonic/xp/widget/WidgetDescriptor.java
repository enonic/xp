package com.enonic.xp.widget;


import java.util.LinkedList;
import java.util.List;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import com.enonic.xp.page.DescriptorKey;

@Beta
public final class WidgetDescriptor
{
    private final DescriptorKey key;

    private final String displayName;

    private final ImmutableList<String> interfaces;

    private WidgetDescriptor( final Builder builder )
    {
        Preconditions.checkNotNull( builder.key, "key cannot be null" );
        this.key = builder.key;
        this.displayName = builder.displayName;
        this.interfaces = ImmutableList.copyOf( builder.interfaces );
    }

    public DescriptorKey getKey()
    {
        return key;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public ImmutableList<String> getInterfaces()
    {
        return interfaces;
    }

    public String getName()
    {
        return this.key.getName();
    }

    public static WidgetDescriptor.Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private DescriptorKey key;

        private String displayName;

        public List<String> interfaces = new LinkedList<>();

        private Builder()
        {
        }

        public Builder key( final DescriptorKey key )
        {
            this.key = key;
            return this;
        }

        public Builder displayName( final String displayName )
        {
            this.displayName = displayName;
            return this;
        }

        public Builder addInterface( final String interfaceName )
        {
            this.interfaces.add( interfaceName );
            return this;
        }

        public WidgetDescriptor build()
        {
            return new WidgetDescriptor( this );
        }
    }
}
